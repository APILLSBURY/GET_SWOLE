package cs65.dartmouth.get_swole;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ScheduleFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_schedule, null, false);
		
		GridView gridview = (GridView) v.findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(container.getContext()));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(container.getContext(), "positiong=" + position + ", id=" + id, Toast.LENGTH_SHORT).show();
	        }
	    });
		
		return v;
	}
	
	private void onPrevClicked(View v) {
		return;
	}
	
	
	private void onNextClicked(View v) {
		return;
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(1, 1, 1, 1);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        
	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	    // references to our images
	    private Integer[] mThumbIds = {
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher,
	            R.drawable.ic_launcher,  R.drawable.ic_launcher,  R.drawable.ic_launcher
	    };
	}
	
}