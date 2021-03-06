/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 Eriks Nukis and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.javascript.checks;

import com.google.common.io.Files;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.api.utils.SonarException;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.CharsetAwareVisitor;
import org.sonar.javascript.lexer.EcmaScriptLexer;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

@Rule(
  key = "TrailingWhitespace",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class TrailingWhitespaceCheck extends SquidCheck<LexerlessGrammar> implements CharsetAwareVisitor {

  private Charset charset;

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public void visitFile(AstNode astNode) {
    List<String> lines;
    try {
      lines = Files.readLines(getContext().getFile(), charset);
    } catch (IOException e) {
      throw new SonarException(e);
    }
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.length() > 0 && Pattern.matches("[" + EcmaScriptLexer.WHITESPACE + "]", line.subSequence(line.length() - 1, line.length()))) {
        getContext().createLineViolation(this, "Remove the useless trailing whitespaces at the end of this line.", i + 1);
      }
    }
  }

}
