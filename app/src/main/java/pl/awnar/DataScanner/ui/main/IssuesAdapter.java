package pl.awnar.DataScanner.ui.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.awnar.DataScanner.api.model.Data;

class IssuesAdapter extends BaseAdapter {
    private Context context;
    List<Data.DataArray> data;

    IssuesAdapter(Context context, List<Data.DataArray> data) {
        this.context = context;
        this.data = data;
    }

    void addItems(List<Data.DataArray> data) {
        this.data.addAll(data);
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
            //convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            //holder.summary = convertView.findViewById(R.id.summary);
            //holder.description = convertView.findViewById(R.id.description);
            //holder.priority = convertView.findViewById(R.id.priority);
            //holder.status = convertView.findViewById(R.id.status);
            //holder.created_at = convertView.findViewById(R.id.created_at);
            //holder.updated_at = convertView.findViewById(R.id.updated_at);
            //holder.projekt = convertView.findViewById(R.id.projekt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.summary.setText(data.get(position).summary);
        //if (data.get(position).description.length() < 120)
        //    holder.description.setText(data.get(position).description);
        //else
        //    holder.description.setText(data.get(position).description.substring(0, 117) + "...");
        //holder.priority.setText(data.get(position).priority.label);
        //holder.status.setText(data.get(position).status.label);
        //holder.created_at.setText(data.get(position).created_at.replaceAll("T|(\\+.*)", " "));
        //holder.updated_at.setText(data.get(position).updated_at.replaceAll("T|(\\+.*)", " "));
        //holder.projekt.setText(data.get(position).project.name);
        return convertView;
    }

    class ViewHolder {
        TextView summary;
        TextView description;
        TextView projekt;
        TextView priority;
        TextView status;
        TextView created_at;
        TextView updated_at;
    }
}
