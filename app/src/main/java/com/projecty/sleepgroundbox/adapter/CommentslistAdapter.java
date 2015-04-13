package com.projecty.sleepgroundbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projecty.sleepgroundbox.R;
import com.projecty.sleepgroundbox.model.item.Comment;
import com.projecty.sleepgroundbox.model.list.CommentList;

/**
 * Created by byungwoo on 15. 4. 11..
 */
public class CommentslistAdapter extends BaseAdapter{

    private final LayoutInflater mInflater;
    private ViewHolder viewHolder;
    private CommentList commentList;

    public CommentslistAdapter(CommentList commentList, LayoutInflater mInflater) {
        this.mInflater = mInflater;
        this.commentList = commentList;
        //JSON 파싱!
    }

    @Override
    public int getCount() {
        return commentList.getCount();
    }

    @Override
    public Comment getItem(int position) {
        return commentList.getComment(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comment_card, null, false);
            viewHolder.detail = (TextView) convertView.findViewById(R.id.comment_detail);
            convertView.setTag(viewHolder);

            Comment comment = getItem(position);

            viewHolder.detail.setText(comment.content);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.detail.setText("댓글댓글 댓글댓글 댓글댓글 댓글댓글 댓글댓글2");


        return convertView;
    }

    private class ViewHolder {

        public TextView detail;
    }

}
