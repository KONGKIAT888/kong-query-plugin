package me.kongkiat.kongquery;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats SQL queries inside JS/TS template literals (backtick strings).
 * Common in node-postgres / pg / TypeORM code, e.g.:
 *
 *   const query = `
 *     UPDATE lead.campaign_info ...
 *   `;
 *
 * The plugin has no hard PSI dependency on the JavaScript plugin, so this
 * works purely on the document text of .js/.jsx/.ts/.tsx files.
 */
public final class JsTsQueryFormatter {

    /** File extensions treated as JavaScript/TypeScript. */
    private static final List<String> JS_TS_EXTENSIONS = List.of(".js", ".jsx", ".ts", ".tsx", ".mjs", ".cjs");

    /** SQL keywords that mark the start of a query inside a template literal. */
    private static final Pattern SQL_START = Pattern.compile(
            "(?is)^\\s*(SELECT|INSERT|UPDATE|DELETE|WITH|REPLACE|CREATE|TRUNCATE|ALTER|DROP)\\b");

    /** ${...} interpolations inside template literals. */
    private static final Pattern INTERPOLATION = Pattern.compile("\\$\\{[^}]*}");

    private static final String INTERPOLATION_TOKEN = "'__KQ_PARAM_";

    private JsTsQueryFormatter() {
    }

    public static boolean isJsTsFile(@NotNull PsiFile psiFile) {
        String name = psiFile.getName().toLowerCase();
        return JS_TS_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    /**
     * Formats all SQL template literals in the file, or only the one at the
     * caret position when {@code onlyCurrentBlock} is true.
     */
    public static void formatQueriesInFile(@NotNull Project project, @NotNull PsiFile psiFile,
                                           Editor editor, boolean onlyCurrentBlock) {
        Document document = editor != null ? editor.getDocument() : psiFile.getViewProvider().getDocument();
        if (document == null) return;

        int caretOffset = onlyCurrentBlock && editor != null
                ? editor.getCaretModel().getOffset() : -1;

        List<int[]> literals = findSqlTemplateLiterals(document.getText());
        if (literals.isEmpty()) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Replace from the end so earlier offsets stay valid
            for (int i = literals.size() - 1; i >= 0; i--) {
                int[] range = literals.get(i);
                if (caretOffset >= 0 && (caretOffset < range[0] || caretOffset > range[1])) continue;

                String rawSql = document.getText()
                        .substring(range[0] + 1, range[1]) // strip backticks
                        .replaceAll("^[\\n\\r]+|[\\n\\r]+$", "");

                String formatted = formatSql(project, rawSql);
                if (!formatted.trim().equals(rawSql.trim())) {
                    document.replaceString(range[0] + 1, range[1], "\n" + formatted.trim() + "\n");
                }
            }
        });
    }

    /**
     * Finds template literals (backtick strings) whose content looks like SQL.
     *
     * @param text Full document text
     * @return List of {openBacktickOffset, closeBacktickOffset} ranges
     */
    static List<int[]> findSqlTemplateLiterals(String text) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            // Skip escaped backticks
            if (c == '\\' && i + 1 < text.length()) {
                i += 2;
                continue;
            }
            if (c != '`') {
                i++;
                continue;
            }
            // Find closing backtick
            int j = i + 1;
            boolean closed = false;
            while (j < text.length()) {
                char cj = text.charAt(j);
                if (cj == '\\' && j + 1 < text.length()) {
                    j += 2;
                    continue;
                }
                if (cj == '`') {
                    closed = true;
                    break;
                }
                j++;
            }
            if (closed && j > i + 1) {
                String content = text.substring(i + 1, j);
                if (SQL_START.matcher(content).find()) {
                    result.add(new int[]{i, j});
                }
                i = j + 1;
            } else {
                break; // unclosed literal - stop scanning
            }
        }
        return result;
    }

    /**
     * Formats SQL content of a template literal. Interpolations (${...}) are
     * temporarily replaced with quoted placeholders so the SQL formatter
     * doesn't choke on them, then restored afterwards.
     */
    private static String formatSql(@NotNull Project project, String sql) {
        try {
            List<String> interpolations = new ArrayList<>();
            Matcher matcher = INTERPOLATION.matcher(sql);
            StringBuffer protectedSql = new StringBuffer();
            while (matcher.find()) {
                interpolations.add(matcher.group());
                matcher.appendReplacement(protectedSql,
                        Matcher.quoteReplacement(INTERPOLATION_TOKEN + interpolations.size() + "__'"));
            }
            matcher.appendTail(protectedSql);

            String formatted = FormatQueryAction.formatQuery(project, protectedSql.toString(), true);

            // Restore interpolations
            for (int k = interpolations.size() - 1; k >= 0; k--) {
                formatted = formatted.replace(INTERPOLATION_TOKEN + (k + 1) + "__'", interpolations.get(k));
            }
            return formatted;
        } catch (Exception e) {
            return sql;
        }
    }
}
