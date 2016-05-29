package com.openclassrooms.fr.premierprojet;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by jsie on 29/05/16.
 */
public class SmbFileAdapter extends ArrayAdapter<SmbFile> {

    public SmbFileAdapter(Context context, List<SmbFile> list) {
        //TODO : check if it's possible to specify another value than 0
        super(context, 0, list);
    }

    /**
     * Format the size with the biggest binary unit.
     *
     * @param size the size to format
     * @return the size with the biggest binary unit, cannot be null.
     */
    public static String formatSize(long size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB"};
        int unit = 0;
        long l = size;

        while ((l / 1024 > 1) && (unit < units.length)) {
            l /= 1024;
            unit++;
        }


        String result = Long.valueOf(l).toString() + " " + units[unit];
        return result;
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

        }

        SmbFile file = getItem(position);

        holder.file.setText(file.getName());
        try {
            if (file.isDirectory()) {
                holder.file.setTextColor(Color.BLUE);
                holder.size.setText("-");
            } else {
                holder.file.setTextColor(Color.GRAY);
                holder.size.setText(formatSize(file.length()));
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }

        convertView.setTag(holder);
        return convertView;
    }

    static class SmbFileComparator implements Comparator<SmbFile> {

        @Override
        public int compare(SmbFile f1, SmbFile f2) {

            try {
                if (f1.isDirectory() && f2.isFile())
                    return -1;

                if (f1.isFile() && f2.isDirectory())
                    return 1;
            } catch (SmbException e) {
                e.printStackTrace();
            }
            return f1.getName().compareTo(f2.getName());
        }
    }

    private class SmbFileHolder {
        public TextView file;
        public TextView size;
    }
}

