package me.kongkiat.kongquery;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Suppresses SQL inspections (dialect/syntax errors like "expected, got 'VALUES'"
 * and unresolved table references like "Unable to resolve table 'VALUES'")
 * inside JS/TS template literals that contain ${...} interpolations.
 *
 * The interpolation is JavaScript code, not SQL, so the injected SQL parser can
 * never validate such strings cleanly — KongQuery handles their formatting itself.
 */
public class JsTsSqlInspectionSuppressor implements InspectionSuppressor {

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        return isInterpolatedJsTsSql(element);
    }

    /**
     * Returns true if the element belongs to SQL injected into a JS/TS
     * template literal that contains ${...} interpolations.
     * Package-private so the highlight error filter can reuse it.
     */
    static boolean isInterpolatedJsTsSql(@NotNull PsiElement element) {
        PsiFile injectedFile = element.getContainingFile();
        if (injectedFile == null) return false;

        Project project = element.getProject();
        PsiElement context = injectedFile.getOriginalFile().getContext();
        PsiFile outerFile = context != null ? context.getContainingFile() : null;
        if (outerFile == null || !JsTsQueryFormatter.isJsTsFile(outerFile)) return false;

        Document document = PsiDocumentManager.getInstance(project).getDocument(outerFile);
        if (document == null) return false;

        // Map the offset inside the injected SQL back to the outer JS/TS document
        TextRange hostRange = InjectedLanguageManager.getInstance(project)
                .injectedToHost(element, element.getTextRange());
        int offset = hostRange.getStartOffset();

        String text = document.getText();
        List<int[]> literals = JsTsQueryFormatter.findSqlTemplateLiterals(text);
        for (int[] range : literals) {
            if (offset >= range[0] && offset <= range[1]) {
                // Only when the literal contains ${...} interpolations,
                // since those make valid SQL parsing impossible
                return text.substring(range[0], range[1] + 1).contains("${");
            }
        }
        return false;
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return new SuppressQuickFix[0];
    }
}
