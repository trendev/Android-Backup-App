package com.openclassrooms.fr.premierprojet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * Created by jsie on 29/05/16.
 */
public class SmbFileAdapter extends ArrayAdapter<SmbFile> {

    public SmbFileAdapter(Context context, List<SmbFile> list) {
        //TODO : check if it's possible to specify another value than 0
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_files, parent, false);

        SmbFileHolder holder = (SmbFileHolder) convertView.getTag();

        if (holder == null) {
            holder = new SmbFileHolder();
            holder.file = (TextView) convertView.findViewById(R.id.filepath);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            convertView.setTag(holder);
        }

        SmbFile file = getItem(position);

        holder.file.setText(file.getCanonicalPath());
        holder.size.setText(Integer.valueOf(file.getContentLength()).toString());

        return convertView;
    }

    private class SmbFileHolder {
        public TextView file;
        public TextView size;
    }
}

