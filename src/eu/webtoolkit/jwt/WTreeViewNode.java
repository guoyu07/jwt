/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.util.EnumSet;
import eu.webtoolkit.jwt.utils.StringUtils;

class WTreeViewNode extends WTable {
	public WTreeViewNode(WTreeView view, WModelIndex index, int childrenHeight,
			boolean isLast, WTreeViewNode parent) {
		super();
		this.view_ = view;
		this.index_ = index;
		this.childrenHeight_ = childrenHeight;
		this.parentNode_ = parent;
		this.childrenLoaded_ = false;
		this.expandIcon_ = null;
		this.noExpandIcon_ = null;
		this.setStyleClass("Wt-tv-node");
		int selfHeight = 0;
		if (!view.isExpanded(this.index_)) {
			this.getRowAt(1).hide();
		}
		boolean needLoad = this.view_.isExpanded(this.index_);
		if (needLoad) {
			this.childrenLoaded_ = true;
			if (this.childrenHeight_ == -1) {
				this.childrenHeight_ = this.view_.subTreeHeight(this.index_)
						- selfHeight;
			}
			if (this.childrenHeight_ > 0) {
				this.setTopSpacerHeight(this.childrenHeight_);
			}
		} else {
			this.childrenHeight_ = 0;
		}
		if (!(this.index_ == this.view_.getRootIndex() || (this.index_ != null && this.index_
				.equals(this.view_.getRootIndex())))) {
			this.getElementAt(0, 1).setStyleClass("c1 rh");
			this.updateGraphics(isLast, this.view_.getModel().getRowCount(
					this.index_) == 0);
			this.insertColumns(0, this.view_.getColumnCount());
			selfHeight = 1;
			if (this.view_.getSelectionBehavior() == SelectionBehavior.SelectRows
					&& this.view_.isSelected(this.index_)) {
				this.renderSelected(true, 0);
			}
		} else {
			this.getElementAt(0, 0).resize(new WLength(1), WLength.Auto);
		}
		this.view_.addRenderedNode(this);
	}

	public void remove() {
		this.view_.removeRenderedNode(this);
		super.remove();
	}

	public void update(int firstColumn, int lastColumn) {
		WModelIndex parent = this.index_.getParent();
		lastColumn = Math.min(lastColumn, this.view_.getModel().getColumnCount(
				parent) - 1);
		for (int i = firstColumn; i <= lastColumn; ++i) {
			WModelIndex child = this.childIndex(i);
			WWidget currentW = this.getWidget(i);
			EnumSet<ViewItemRenderFlag> renderFlags = EnumSet
					.noneOf(ViewItemRenderFlag.class);
			if (this.view_.getSelectionBehavior() == SelectionBehavior.SelectItems
					&& this.view_.isSelected(child)) {
				renderFlags.add(ViewItemRenderFlag.RenderSelected);
			}
			WWidget newW = this.view_.getItemDelegate(child).update(currentW,
					child, renderFlags);
			if (newW != currentW) {
				this.setWidget(i, newW, currentW != null);
			}
		}
	}

	public void updateGraphics(boolean isLast, boolean isEmpty) {
		if ((this.index_ == this.view_.getRootIndex() || (this.index_ != null && this.index_
				.equals(this.view_.getRootIndex())))) {
			return;
		}
		if ((this.index_.getParent() == this.view_.getRootIndex() || (this.index_
				.getParent() != null && this.index_.getParent().equals(
				this.view_.getRootIndex())))
				&& !this.view_.isRootDecorated()) {
			if (this.expandIcon_ != null)
				this.expandIcon_.remove();
			this.expandIcon_ = null;
			if (this.noExpandIcon_ != null)
				this.noExpandIcon_.remove();
			this.noExpandIcon_ = null;
			this.getElementAt(0, 0).setStyleClass("c0");
			this.getElementAt(1, 0).setStyleClass("c0");
			return;
		}
		if (!isEmpty) {
			if (!(this.expandIcon_ != null)) {
				if (this.noExpandIcon_ != null)
					this.noExpandIcon_.remove();
				this.noExpandIcon_ = null;
				this.expandIcon_ = new WIconPair(this.view_.getImagePack()
						+ "nav-plus.gif", this.view_.getImagePack()
						+ "nav-minus.gif");
				this.getElementAt(0, 0).addWidget(this.expandIcon_);
				this.expandIcon_.icon1Clicked().addListener(this,
						new Signal1.Listener<WMouseEvent>() {
							public void trigger(WMouseEvent e1) {
								WTreeViewNode.this.doExpand();
							}
						});
				this.expandIcon_.icon2Clicked().addListener(this,
						new Signal1.Listener<WMouseEvent>() {
							public void trigger(WMouseEvent e1) {
								WTreeViewNode.this.doCollapse();
							}
						});
				if (this.isExpanded()) {
					this.expandIcon_.setState(1);
				}
			}
		} else {
			if (!(this.noExpandIcon_ != null)) {
				if (this.expandIcon_ != null)
					this.expandIcon_.remove();
				this.expandIcon_ = null;
				this.noExpandIcon_ = new WImage(this.view_.getImagePack()
						+ "line-middle.gif");
				this.getElementAt(0, 0).addWidget(this.noExpandIcon_);
			}
		}
		if (!isLast) {
			this.getElementAt(0, 0).setStyleClass("Wt-trunk c0");
			this.getElementAt(1, 0).setStyleClass("Wt-trunk c0");
		} else {
			this.getElementAt(0, 0).setStyleClass("Wt-end c0");
			this.getElementAt(1, 0).setStyleClass("c0");
		}
	}

	public void insertColumns(int column, int count) {
		WTableCell tc = this.getElementAt(0, 1);
		tc.clear();
		if (this.view_.getColumnCount() > 1) {
			WContainerWidget row = new WContainerWidget();
			if (this.view_.column1Fixed_) {
				row.setStyleClass("Wt-tv-rowc rh");
				WContainerWidget rowWrap = new WContainerWidget();
				rowWrap.addWidget(row);
				row = rowWrap;
			}
			row.setStyleClass("Wt-tv-row rh");
			tc.insertWidget(0, row);
		}
		this.update(0, this.view_.getColumnCount() - 1);
	}

	public void removeColumns(int column, int count) {
		this.insertColumns(0, 0);
	}

	public boolean isLast() {
		return (this.index_ == this.view_.getRootIndex() || (this.index_ != null && this.index_
				.equals(this.view_.getRootIndex())))
				|| this.index_.getRow() == this.view_.getModel().getRowCount(
						this.index_.getParent()) - 1;
	}

	public void rerenderSpacers() {
		RowSpacer s = this.topSpacer();
		if (s != null) {
			s.setRows(this.getTopSpacerHeight(), true);
		}
		s = this.bottomSpacer();
		if (s != null) {
			s.setRows(this.getBottomSpacerHeight(), true);
		}
	}

	public WModelIndex getModelIndex() {
		return this.index_;
	}

	public int getChildrenHeight() {
		return this.childrenHeight_;
	}

	public int getRenderedHeight() {
		return (!(this.index_ == this.view_.getRootIndex() || (this.index_ != null && this.index_
				.equals(this.view_.getRootIndex()))) ? 1 : 0)
				+ (this.isExpanded() ? this.childrenHeight_ : 0);
	}

	public boolean isChildrenLoaded() {
		return this.childrenLoaded_;
	}

	public WWidget widgetForModelRow(int modelRow) {
		if (!this.childrenLoaded_) {
			return null;
		}
		for (WTreeViewNode c = this.nextChildNode((WTreeViewNode) null); c != null; c = this
				.nextChildNode(c)) {
			if (c.index_.getRow() > modelRow) {
				return this.topSpacer();
			} else {
				if (c.index_.getRow() == modelRow) {
					return c;
				}
			}
		}
		return this.bottomSpacer();
	}

	public WTreeViewNode nextChildNode(WTreeViewNode prev) {
		if (!this.childrenLoaded_) {
			return null;
		}
		WContainerWidget c = this.getChildContainer();
		int nextI = prev != null ? c.getIndexOf(prev) + 1
				: this.topSpacer() != null ? 1 : 0;
		if (nextI < c.getCount()) {
			return ((c.getWidget(nextI)) instanceof WTreeViewNode ? (WTreeViewNode) (c
					.getWidget(nextI))
					: null);
		} else {
			return null;
		}
	}

	public boolean isAllSpacer() {
		return this.childrenLoaded_ && this.topSpacer() != null
				&& this.topSpacer() == this.bottomSpacer();
	}

	public void setTopSpacerHeight(int rows) {
		if (rows == 0) {
			if (this.topSpacer() != null)
				this.topSpacer().remove();
		} else {
			this.topSpacer(true).setRows(rows);
		}
	}

	public void addTopSpacerHeight(int rows) {
		this.setTopSpacerHeight(this.getTopSpacerHeight() + rows);
	}

	public int getTopSpacerHeight() {
		RowSpacer s = this.topSpacer();
		return s != null ? s.getRows() : 0;
	}

	public void setBottomSpacerHeight(int rows) {
		if (!(rows != 0)) {
			if (this.bottomSpacer() != null)
				this.bottomSpacer().remove();
		} else {
			this.bottomSpacer(true).setRows(rows);
		}
	}

	public void addBottomSpacerHeight(int rows) {
		this.setBottomSpacerHeight(this.getBottomSpacerHeight() + rows);
	}

	public int getBottomSpacerHeight() {
		RowSpacer s = this.bottomSpacer();
		return s != null ? s.getRows() : 0;
	}

	public RowSpacer topSpacer(boolean create) {
		WContainerWidget c = this.getChildContainer();
		RowSpacer result = null;
		if (c.getCount() == 0
				|| !((result = ((c.getWidget(0)) instanceof RowSpacer ? (RowSpacer) (c
						.getWidget(0))
						: null)) != null)) {
			if (!create) {
				return null;
			} else {
				result = new RowSpacer(this, 0);
				c.insertWidget(0, result);
			}
		}
		return result;
	}

	public final RowSpacer topSpacer() {
		return topSpacer(false);
	}

	public RowSpacer bottomSpacer(boolean create) {
		WContainerWidget c = this.getChildContainer();
		RowSpacer result = null;
		if (c.getCount() == 0
				|| !((result = ((c.getWidget(c.getCount() - 1)) instanceof RowSpacer ? (RowSpacer) (c
						.getWidget(c.getCount() - 1))
						: null)) != null)) {
			if (!create) {
				return null;
			} else {
				result = new RowSpacer(this, 0);
				c.addWidget(result);
			}
		}
		return result;
	}

	public final RowSpacer bottomSpacer() {
		return bottomSpacer(false);
	}

	public WContainerWidget getChildContainer() {
		return this
				.getElementAt(
						(this.index_ == this.view_.getRootIndex() || (this.index_ != null && this.index_
								.equals(this.view_.getRootIndex()))) ? 0 : 1, 1);
	}

	public void shiftModelIndexes(int start, int offset) {
		if (!this.childrenLoaded_) {
			return;
		}
		WContainerWidget c = this.getChildContainer();
		int first;
		int end;
		int inc;
		if (offset > 0) {
			first = c.getCount() - 1;
			end = -1;
			inc = -1;
		} else {
			first = 0;
			end = c.getCount();
			inc = 1;
		}
		for (int i = first; i != end; i += inc) {
			WTreeViewNode n = ((c.getWidget(i)) instanceof WTreeViewNode ? (WTreeViewNode) (c
					.getWidget(i))
					: null);
			if (n != null && n.getModelIndex().getRow() >= start) {
				this.view_.removeRenderedNode(n);
				n.index_ = this.view_.getModel().getIndex(
						n.getModelIndex().getRow() + offset,
						n.getModelIndex().getColumn(), this.index_);
				int lastColumn = Math.min(this.view_.getColumnCount() - 1,
						this.view_.getModel().getColumnCount(this.index_) - 1);
				for (int j = 0; j <= lastColumn; ++j) {
					WModelIndex child = n.childIndex(j);
					this.view_.getItemDelegate(child).updateModelIndex(
							n.getWidget(j), child);
				}
				this.view_.addRenderedNode(n);
			}
		}
	}

	public WTreeViewNode getParentNode() {
		return this.parentNode_;
	}

	public boolean isExpanded() {
		return !this.getRowAt(1).isHidden();
	}

	public void adjustChildrenHeight(int diff) {
		this.childrenHeight_ += diff;
		if (this.isExpanded()) {
			WTreeViewNode parent = this.getParentNode();
			if (parent != null) {
				parent.adjustChildrenHeight(diff);
			}
		}
	}

	public void normalizeSpacers() {
		if (this.childrenLoaded_ && this.getChildContainer().getCount() == 2
				&& this.topSpacer() != null && this.bottomSpacer() != null) {
			this.addTopSpacerHeight(this.getBottomSpacerHeight());
			if (this.bottomSpacer() != null)
				this.bottomSpacer().remove();
		}
	}

	public void selfCheck() {
		assert this.getRenderedHeight() == this.view_
				.subTreeHeight(this.index_);
		int childNodesHeight = 0;
		for (WTreeViewNode c = this.nextChildNode((WTreeViewNode) null); c != null; c = this
				.nextChildNode(c)) {
			c.selfCheck();
			childNodesHeight += c.getRenderedHeight();
		}
		if (childNodesHeight == 0) {
			assert this.topSpacer() == this.bottomSpacer();
			assert this.getTopSpacerHeight() == this.getChildrenHeight();
		} else {
			assert this.getTopSpacerHeight() + childNodesHeight
					+ this.getBottomSpacerHeight() == this.getChildrenHeight();
		}
	}

	public WTreeView getView() {
		return this.view_;
	}

	public int renderedRow(int lowerBound, int upperBound) {
		if (!(this.parentNode_ != null)) {
			return 0;
		} else {
			int result = this.parentNode_.renderedRow(0, upperBound);
			if (result > upperBound) {
				return result;
			}
			return result
					+ this.parentNode_.renderedRow(this, lowerBound - result,
							upperBound - result);
		}
	}

	public final int renderedRow() {
		return renderedRow(0, Integer.MAX_VALUE);
	}

	public final int renderedRow(int lowerBound) {
		return renderedRow(lowerBound, Integer.MAX_VALUE);
	}

	public int renderedRow(WTreeViewNode node, int lowerBound, int upperBound) {
		if (this.getRenderedHeight() < lowerBound) {
			return this.getRenderedHeight();
		}
		int result = this.getTopSpacerHeight();
		if (result > upperBound) {
			return result;
		}
		for (WTreeViewNode c = this.nextChildNode((WTreeViewNode) null); c != null; c = this
				.nextChildNode(c)) {
			if (c == node) {
				return result;
			} else {
				result += c.getRenderedHeight();
				if (result > upperBound) {
					return result;
				}
			}
		}
		assert false;
		return 0;
	}

	public final int renderedRow(WTreeViewNode node) {
		return renderedRow(node, 0, Integer.MAX_VALUE);
	}

	public final int renderedRow(WTreeViewNode node, int lowerBound) {
		return renderedRow(node, lowerBound, Integer.MAX_VALUE);
	}

	public void renderSelected(boolean selected, int column) {
		if (this.view_.getSelectionBehavior() == SelectionBehavior.SelectRows) {
			this.getRowAt(0).setStyleClass(selected ? "selected" : "");
		} else {
			WWidget w = this.getWidget(column);
			if (selected) {
				w.setStyleClass(StringUtils.addWord(w.getStyleClass(),
						"selected"));
			} else {
				w.setStyleClass(StringUtils.eraseWord(w.getStyleClass(),
						"selected"));
			}
		}
	}

	public void doExpand() {
		if (this.isExpanded()) {
			return;
		}
		this.loadChildren();
		if (this.expandIcon_ != null) {
			this.expandIcon_.setState(1);
		}
		this.view_.expandedSet_.add(this.index_);
		this.getRowAt(1).show();
		if (this.getParentNode() != null) {
			this.getParentNode().adjustChildrenHeight(this.childrenHeight_);
		}
		this.view_.adjustRenderedNode(this, this.renderedRow());
		this.view_.scheduleRerender(WTreeView.RenderState.NeedAdjustViewPort);
		this.view_.expanded_.trigger(this.index_);
	}

	public void doCollapse() {
		if (!this.isExpanded()) {
			return;
		}
		if (this.expandIcon_ != null) {
			this.expandIcon_.setState(0);
		}
		this.view_.setCollapsed(this.index_);
		this.getRowAt(1).hide();
		if (this.getParentNode() != null) {
			this.getParentNode().adjustChildrenHeight(-this.childrenHeight_);
		}
		this.view_.renderedRowsChanged(this.renderedRow(),
				-this.childrenHeight_);
		this.view_.collapsed_.trigger(this.index_);
	}

	private WTreeView view_;
	private WModelIndex index_;
	private int childrenHeight_;
	private WTreeViewNode parentNode_;
	private boolean childrenLoaded_;
	private WIconPair expandIcon_;
	private WImage noExpandIcon_;

	private void loadChildren() {
		if (!this.childrenLoaded_) {
			this.childrenLoaded_ = true;
			this.view_.expandedSet_.add(this.index_);
			this.childrenHeight_ = this.view_.subTreeHeight(this.index_) - 1;
			this.view_.expandedSet_.remove(this.index_);
			if (this.childrenHeight_ > 0) {
				this.setTopSpacerHeight(this.childrenHeight_);
			}
		}
	}

	private WModelIndex childIndex(int column) {
		return this.view_.getModel().getIndex(this.index_.getRow(), column,
				this.index_.getParent());
	}

	private WWidget getWidget(int column) {
		WTableCell tc = this.getElementAt(0, 1);
		if (column == 0) {
			return tc.getCount() > 1 ? tc.getWidget(tc.getCount() - 1) : null;
		} else {
			WContainerWidget row = ((tc.getWidget(0)) instanceof WContainerWidget ? (WContainerWidget) (tc
					.getWidget(0))
					: null);
			if (this.view_.column1Fixed_) {
				row = ((row.getWidget(0)) instanceof WContainerWidget ? (WContainerWidget) (row
						.getWidget(0))
						: null);
			}
			return row.getCount() >= column ? row.getWidget(column - 1) : null;
		}
	}

	private void setWidget(int column, WWidget newW, boolean replace) {
		WTableCell tc = this.getElementAt(0, 1);
		WWidget current = replace ? this.getWidget(column) : null;
		if (current != null) {
			newW.setStyleClass(current.getStyleClass());
			current.setStyleClass(new WString().toString());
		} else {
			EscapeOStream s = new EscapeOStream();
			s.append("Wt-tv-c rh ").append(
					this.view_.getColumnStyleClass(column)).append(' ').append(
					newW.getStyleClass());
			newW.setStyleClass(new WString(s.toString()).toString());
		}
		if (column == 0) {
			if (current != null) {
				tc.removeWidget(current);
			}
			tc.addWidget(newW);
		} else {
			WContainerWidget row = ((tc.getWidget(0)) instanceof WContainerWidget ? (WContainerWidget) (tc
					.getWidget(0))
					: null);
			if (this.view_.column1Fixed_) {
				row = ((row.getWidget(0)) instanceof WContainerWidget ? (WContainerWidget) (row
						.getWidget(0))
						: null);
			}
			if (current != null) {
				row.removeWidget(current);
			}
			row.insertWidget(column - 1, newW);
		}
		if (!WApplication.getInstance().getEnvironment().hasAjax()) {
			WInteractWidget wi = ((newW) instanceof WInteractWidget ? (WInteractWidget) (newW)
					: null);
			if (wi != null) {
				this.view_.clickedMapper_.mapConnect(wi.clicked(), this
						.childIndex(column));
			}
		}
	}
}
