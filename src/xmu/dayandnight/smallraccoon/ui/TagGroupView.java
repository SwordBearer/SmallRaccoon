package xmu.dayandnight.smallraccoon.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xmu.dayandnight.smallraccoon.R;

/**
 * @author SwordBearer  e-mail :ranxiedao@163.com
 *         Created by SwordBearer on 08/04/2014
 */

/**
 * 自适应的TAG容器控件，显示多个文本内容的TAG标签
 */
public class TagGroupView extends AdaptiveViewGroup {

    private List<String> tags;

    private boolean isDeletable = false;// 是否可以长按删除
    private boolean isUnderDelete = false;// 是否处在删除状态

    private boolean canRepeat = false;// tag是否可以重复

    private void initValues() {
        this.setChildMargin(6);
        this.tags = new ArrayList<String>();
        this.isUnderDelete = false;
        this.isDeletable = false;
        this.canRepeat = false;
    }

    public TagGroupView(Context context) {
        super(context);
        initValues();
    }

    public TagGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValues();
    }

    public TagGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initValues();
    }

    public boolean isCanRepeat() {
        return canRepeat;
    }

    public void setCanRepeat(boolean canRepeat) {
        this.canRepeat = canRepeat;
    }

    public boolean isUnderDelete() {
        return isUnderDelete;
    }

    public void setUnderDelete(boolean isUnderDelete) {
        this.isUnderDelete = isUnderDelete;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    /**
     * 是否可以长按删除tag，默认为true
     * 
     * @param isDeletable
     */
    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    /**
     * 设置tag列表
     * 
     * @param data
     */
    public void setTags(List<String> data) {
        if (data == null) {
            throw new NullPointerException("tags data can not be null !");
        }
        this.tags.clear();
        this.tags = data;
        refresh();
    }

    /**
     * 增加一个tag标签 , 注意：不能添加重复的标签
     * 
     * @param tag
     */
    public boolean addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<String>();
        }
        if (!this.canRepeat && containsTag(tag)) {
            return false;
        }
        this.tags.add(tag);
        refresh();
        return true;
    }

    /**
     * 是否包含tag
     * 
     * @param tag
     * @return TRUE if contains, otherwise FALSE
     */
    public boolean containsTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * 删除指定位置的tag
     * 
     * @param index
     *            tag的索引，以0为起点
     */
    public void removeTag(int index) {
        if (index >= this.tags.size()) {
            throw new IndexOutOfBoundsException(
                    "index is larger than tags count");
        }
        this.tags.remove(index);
        refresh();
    }

    /**
     * 获得所有的tag
     * 
     * @return
     */
    public List<String> getTags() {
        return this.tags;
    }

    public int getTagCount() {
        return this.tags.size();
    }

    /**
     * 获得某一位置的tag
     * 
     * @param index
     *            tag的索引，以0为起点
     * @return
     */
    public String getTagAt(int index) {
        if (index >= this.tags.size()) {
            throw new IndexOutOfBoundsException(
                    "index is larger than tags count");
        }
        return this.tags.get(index);
    }

    /**
     * 显示所有的tag
     */
    private void refresh() {
        if (tags == null) {
            return;
        }
        this.removeAllViews();
        int count = this.tags.size();
        Log.e("TagGroupView", " 总共的TAG有 " + tags.size());
        String value = null;

        for (int index = 0; index < count; index++) {
            value = this.tags.get(index);
            View child = View.inflate(getContext(), R.layout.item_tag_view,
                    null);
            if (child == null) {
                throw new NullPointerException("Tag View can not be null !");
            }
            child.setClickable(true);
            child.setFocusable(true);
            ImageButton delBtn = (ImageButton) child
                    .findViewById(R.id.tagview_del_btn);
            TextView text = (TextView) child.findViewById(R.id.tagview_text);
            text.setText(value);
            delBtn.setOnClickListener(delTagListener);
            if (!isUnderDelete) {
                delBtn.setVisibility(View.INVISIBLE);
            } else {
                delBtn.setVisibility(View.VISIBLE);
            }
            child.setTag(index);
            delBtn.setTag(index);
            child.setOnLongClickListener(longClickChildListener);
            addView(child);
        }
        Log.e("TagGroupView", " 总共的View有 " + getChildCount());
    }

    /**
     * 是否显示删除按钮
     * 
     * @param flag
     */
    public void showDeleteButton(boolean flag) {
        this.isUnderDelete = flag;
        if (tags == null) {
            return;
        }
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ImageButton delBtn = (ImageButton) child
                    .findViewById(R.id.tagview_del_btn);
            if (isUnderDelete) {
                delBtn.setVisibility(View.VISIBLE);
            } else {
                delBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    private OnClickListener delTagListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            removeTag(index);
        }
    };

    private OnLongClickListener longClickChildListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (isDeletable) {
                showDeleteButton(true);
            }
            return false;
        }
    };
}

