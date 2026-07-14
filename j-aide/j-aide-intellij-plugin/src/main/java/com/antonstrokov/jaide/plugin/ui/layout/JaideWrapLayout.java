package com.antonstrokov.jaide.plugin.ui.layout;

import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;

public final class JaideWrapLayout extends FlowLayout {

	public JaideWrapLayout(
			@MagicConstant(intValues = {
					FlowLayout.LEFT,
					FlowLayout.CENTER,
					FlowLayout.RIGHT,
					FlowLayout.LEADING,
					FlowLayout.TRAILING
			})
			int alignment,
			int horizontalGap,
			int verticalGap
	) {
		super(alignment, horizontalGap, verticalGap);
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		return calculateLayoutSize(target, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		return calculateLayoutSize(target, false);
	}

	private Dimension calculateLayoutSize(
			Container target,
			boolean usePreferredSize
	) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();

			int targetWidth = target.getWidth();

			if (targetWidth <= 0 && target.getParent() != null) {
				targetWidth = target.getParent().getWidth();
			}

			if (targetWidth <= 0) {
				targetWidth = Integer.MAX_VALUE;
			}

			int horizontalInsets =
					insets.left
							+ insets.right
							+ getHgap() * 2;

			int availableRowWidth =
					targetWidth - horizontalInsets;

			Dimension layoutSize = new Dimension();

			int rowWidth = 0;
			int rowHeight = 0;

			for (Component component : target.getComponents()) {
				if (!component.isVisible()) {
					continue;
				}

				Dimension componentSize = usePreferredSize
						? component.getPreferredSize()
						: component.getMinimumSize();

				boolean requiresNewRow =
						rowWidth > 0
								&& rowWidth
								+ getHgap()
								+ componentSize.width
								> availableRowWidth;

				if (requiresNewRow) {
					addRow(
							layoutSize,
							rowWidth,
							rowHeight
					);

					rowWidth = 0;
					rowHeight = 0;
				}

				if (rowWidth > 0) {
					rowWidth += getHgap();
				}

				rowWidth += componentSize.width;
				rowHeight = Math.max(
						rowHeight,
						componentSize.height
				);
			}

			if (rowWidth > 0) {
				addRow(
						layoutSize,
						rowWidth,
						rowHeight
				);
			}

			layoutSize.width += horizontalInsets;
			layoutSize.height +=
					insets.top
							+ insets.bottom
							+ getVgap() * 2;

			return layoutSize;
		}
	}

	private void addRow(
			Dimension layoutSize,
			int rowWidth,
			int rowHeight
	) {
		layoutSize.width = Math.max(
				layoutSize.width,
				rowWidth
		);

		if (layoutSize.height > 0) {
			layoutSize.height += getVgap();
		}

		layoutSize.height += rowHeight;
	}
}
