/**
 * Copyright (c) 2011, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.codenarc;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import com.ymock.util.Logger;
import java.util.List;
import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.results.Results;
import org.codenarc.rule.Violation;

/**
 * Validates groovy source code with CodeNarc.
 *
 * @author Pavlo Shamrai (pshamrai@gmail.com)
 * @version $Id: CodeNarcValidator.java 45 2011-10-27 19:34:11Z pshamrai@gmail.com $
 *
 */

public final class CodeNarcValidator implements Validator {

    @Override
    public void validate(final Environment env) throws ValidationException {
        final FilesystemSourceAnalyzer sourceAnalyzer =
            new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(
            env.basedir().getAbsolutePath()
        );
        sourceAnalyzer.setIncludes("**/*.groovy");
        sourceAnalyzer.setExcludes(null);
        final CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setSourceAnalyzer(sourceAnalyzer);
        codeNarcRunner.setRuleSetFiles(
            "com/qulice/codenarc/"
            + "StarterRuleSet-AllRulesByCategory.groovy.txt"
        );
        codeNarcRunner.setReportWriters(null);
        final Results results = codeNarcRunner.execute();
        final List<Violation> violations = results.getViolations();
        this.logViolations(violations);
        if (!violations.isEmpty()) {
            throw new ValidationException(
                "%d CodeNarc violations (see log above)",
                violations.size()
            );
        }
        Logger.info(
            this,
            "No CodeNarc violations found in %d files",
            results.getTotalNumberOfFiles(true)
        );
    }

        /**
        * Log all violations.
        *
        * @param violations The list of violations
        */
    private void logViolations(final List<Violation> violations) {
        for (Violation violation : violations) {
            Logger.info(this, violation.toString());
        }
    }
}