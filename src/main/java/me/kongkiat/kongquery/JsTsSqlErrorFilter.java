package me.kongkiat.kongquery;

import com.intellij.codeInsight.highlighting.HighlightErrorFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import org.jetbrains.annotations.NotNull;

/**
 * Suppresses SQL parser error highlighting (e.g.
 * "&lt;query expression&gt;, DEFAULT, ON, ... expected, got 'VALUES'")
 * inside JS/TS template literals that contain ${...} interpolations.
 *
 * Parse errors are produced by the daemon highlighting pass, not by any
 * inspection, so a {@code lang.inspectionSuppressor} alone cannot hide them.
 * This uses the same mechanism the JavaScript plugin uses for interpolated
 * strings ({@code StringInterpolationErrorFilter}).
 */
public class JsTsSqlErrorFilter extends HighlightErrorFilter {

    @Override
    public boolean shouldHighlightErrorElement(@NotNull PsiErrorElement element) {
        return !JsTsSqlInspectionSuppressor.isInterpolatedJsTsSql(element);
    }
}
