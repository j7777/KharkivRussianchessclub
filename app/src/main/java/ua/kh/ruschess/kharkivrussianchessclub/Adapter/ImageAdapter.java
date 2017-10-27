package ua.kh.ruschess.kharkivrussianchessclub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public	String[] mThumbIdsStr = new String[24];

    public ImageAdapter(Context c, String[] arr) {
        mContext = c;

        if(arr.length > 0) {
            mThumbIdsStr[0] = arr[0];
            mThumbIdsStr[1] = arr[1];
            mThumbIdsStr[2] = arr[1];
            mThumbIdsStr[3] = arr[1];
            mThumbIdsStr[4] = arr[1];
            mThumbIdsStr[5] = arr[1];
            mThumbIdsStr[6] = arr[1];
            mThumbIdsStr[7] = arr[1];
            mThumbIdsStr[8] = arr[1];
            mThumbIdsStr[9] = arr[1];
            mThumbIdsStr[10] = arr[2];
            mThumbIdsStr[11] = arr[2];
            mThumbIdsStr[12] = arr[2];
            mThumbIdsStr[13] = arr[3];
            mThumbIdsStr[14] = arr[3];
            mThumbIdsStr[15] = arr[3];
            mThumbIdsStr[16] = arr[3];
            mThumbIdsStr[17] = arr[4];
            mThumbIdsStr[18] = arr[4];
            mThumbIdsStr[19] = arr[4];
            mThumbIdsStr[20] = arr[5];
            mThumbIdsStr[21] = arr[5];
            mThumbIdsStr[22] = arr[6];
            mThumbIdsStr[23] = arr[6];
        }
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View grid;

        if (convertView == null) {
            grid = new View(mContext);
            //LayoutInflater inflater = getLayoutInflater();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            grid = inflater.inflate(R.layout.cellgrid, parent, false);
        } else {
            grid = (View) convertView;
        }

        ImageView imageView = (ImageView) grid.findViewById(R.id.imagepart);
        TextView textView = (TextView) grid.findViewById(R.id.textpart);
        imageView.setImageResource(mThumbIds[position]);

        if(mThumbIdsStr.length > 0) {
            textView.setText(mThumbIdsStr[position]);
        }

        return grid;
    }

    // references to our images
    public	Integer[] mThumbIds = {
            R.drawable.rules_general,
            R.drawable.rules_pawn1,
            R.drawable.rules_pawn2,
            R.drawable.rules_pawn3,
            R.drawable.rules_pawn4,
            R.drawable.rules_pawn5,
            R.drawable.rules_pawn6,
            R.drawable.rules_pawn7,
            R.drawable.rules_pawn8,
            R.drawable.rules_pawn9,
            R.drawable.rules_rook1,
            R.drawable.rules_rook2,
            R.drawable.rules_rook3,
            R.drawable.rules_horse1,
            R.drawable.rules_horse2,
            R.drawable.rules_horse3,
            R.drawable.rules_horse4,
            R.drawable.rules_officer1,
            R.drawable.rules_officer2,
            R.drawable.rules_officer3,
            R.drawable.rules_queen1,
            R.drawable.rules_queen2,
            R.drawable.rules_king1,
            R.drawable.rules_king2};

    public	String[] mThumbIdsName = {
            "rules_general",
            "rules_pawn1",
            "rules_pawn2",
            "rules_pawn3",
            "rules_pawn4",
            "rules_pawn5",
            "rules_pawn6",
            "rules_pawn7",
            "rules_pawn8",
            "rules_pawn9",
            "rules_rook1",
            "rules_rook2",
            "rules_rook3",
            "rules_horse1",
            "rules_horse2",
            "rules_horse3",
            "rules_horse4",
            "rules_officer1",
            "rules_officer2",
            "rules_officer3",
            "rules_queen1",
            "rules_queen2",
            "rules_king1",
            "rules_king2"};
}