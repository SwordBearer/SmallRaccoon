package xmu.swordbearer.smallraccoon.widget;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UIUtils {
	/**
	 * 去掉ListView的滚动条，用于解决和ScrollView的冲突
	 * 
	 * @param listView
	 * @return
	 */
	private ViewGroup.LayoutParams setListViewHeightBasedOnChildren(
			ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return new ViewGroup.LayoutParams(0, 0);
		}
		View view = null;
		int totalHeight = listView.getPaddingTop()
				+ listView.getPaddingBottom();
		int desiredWidth = MeasureSpec.makeMeasureSpec(context.
				.getWindowManager().getDefaultDisplay().getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, null, listView);
			if (view != null) {
				view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
				totalHeight += view.getMeasuredHeight();
			}
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount()));
		listView.setLayoutParams(params);
		listView.requestLayout();
		return params;
	}
}
