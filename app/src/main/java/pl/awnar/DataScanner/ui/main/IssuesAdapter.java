package pl.awnar.DataScanner.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import pl.awnar.DataScanner.R;
import pl.awnar.DataScanner.api.model.Data;

class IssuesAdapter extends BaseAdapter {
    private Context context;
    List<Data.DataArray> data;

    IssuesAdapter(Context context, List<Data.DataArray> data) {
        this.context = context;
        this.data = data;
    }

    void setItems(List<Data.DataArray> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    private void replaceView(View oldV, View newV) {
        ViewGroup par = (ViewGroup) oldV.getParent();
        if (par == null) {
            return;
        }
        int i1 = par.indexOfChild(oldV);
        par.removeViewAt(i1);
        par.addView(newV, i1);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder.create = convertView.findViewById(R.id.create);
            LinearLayout layout = convertView.findViewById(R.id.linearLayout);

            int tmp = layout.getHeight();

            switch (data.get(position).in_blob_type) {
                case "TXT":
                    holder.in = new TextView(context);
                    break;
                case "IMG":
                    holder.in = new ImageView(context);
                    break;
            }
            switch (data.get(position).out_blob_type) {
                case "TXT":
                    holder.out = new TextView(context);
                    break;
                case "IMG":
                    holder.out = new ImageView(context);
                    break;
            }

            replaceView(convertView.findViewById(R.id.view), holder.in);
            replaceView(convertView.findViewById(R.id.view2), holder.out);

            ViewGroup.LayoutParams tmp2 = holder.in.getLayoutParams();
            //tmp2.height = tmp/2;
            holder.in.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            tmp2 = holder.out.getLayoutParams();
            //tmp2.height = tmp/2;
            holder.out.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.create.setText(data.get(position).create);

        if (data.get(position).in_blob != null)
            switch (data.get(position).in_blob_type) {
                case "TXT":
                    if (data.get(position).in_blob.length() < 120)
                        ((TextView) holder.in).setText(data.get(position).in_blob);
                    else
                        ((TextView) holder.in).setText(data.get(position).in_blob.substring(0, 117) + "...");
                    break;
                case "IMG":
                    byte[] decodedString = Base64.decode(data.get(position).in_blob, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ((ImageView) holder.in).setImageBitmap(decodedByte);
            }

        if (data.get(position).out_blob != null)
            switch (data.get(position).out_blob_type) {
                case "TXT":
                    if (data.get(position).out_blob.length() < 120)
                        ((TextView) holder.out).setText(data.get(position).out_blob);
                    else
                        ((TextView) holder.out).setText(data.get(position).out_blob.substring(0, 117) + "...");
                    break;
                case "IMG":
                    byte[] decodedString = Base64.decode(data.get(position).out_blob, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ((ImageView) holder.out).setImageBitmap(decodedByte);
            }
        return convertView;
    }

    class ViewHolder {
        TextView create;
        View in;
        View out;
    }
}
