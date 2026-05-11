package com.antonstrokov.jaide.plugin.ui.improve;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.JTextArea;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class JaideImprovePreviewPanel extends JPanel {

	private final JTextArea textArea;

	public JaideImprovePreviewPanel(String initialText) {
		super(new BorderLayout());

		textArea = new JTextArea(initialText);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(
				EditorColorsManager.getInstance()
						.getGlobalScheme()
						.getFont(EditorFontType.PLAIN)
		);
		textArea.setBackground(JBColor.PanelBackground);
		textArea.setForeground(JBColor.foreground());
		textArea.setBorder(JBUI.Borders.empty(12));
		textArea.setTabSize(4);

		add(new JBScrollPane(textArea), BorderLayout.CENTER);
	}

	public void updateText(String text) {
		textArea.setText(text);
		textArea.setCaretPosition(0);
	}
}