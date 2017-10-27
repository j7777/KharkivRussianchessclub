package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ua.kh.ruschess.kharkivrussianchessclub.Adapter.ImageAdapter;
import ua.kh.ruschess.kharkivrussianchessclub.R;
import uk.co.senab.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();

        int position = intent.getExtras().getInt("id");
        String[] arr = new String[0];
        ImageAdapter imageAdapter = new ImageAdapter(this, arr);

        PhotoView photoView = (PhotoView)findViewById(R.id.iv_photo);

        switch (imageAdapter.mThumbIdsName[position]){
            case "rules_general":
                photoView.setImageResource(R.drawable.full_rules_general);
                break;
            case "rules_horse1":
                photoView.setImageResource(R.drawable.full_rules_horse1);
                break;
            case "rules_horse2":
                photoView.setImageResource(R.drawable.full_rules_horse2);
                break;
            case "rules_horse3":
                photoView.setImageResource(R.drawable.full_rules_horse3);
                break;
            case "rules_horse4":
                photoView.setImageResource(R.drawable.full_rules_horse4);
                break;
            case "rules_king1":
                photoView.setImageResource(R.drawable.full_rules_king1);
                break;
            case "rules_king2":
                photoView.setImageResource(R.drawable.full_rules_king2);
                break;
            case "rules_officer1":
                photoView.setImageResource(R.drawable.full_rules_officer1);
                break;
            case "rules_officer2":
                photoView.setImageResource(R.drawable.full_rules_officer2);
                break;
            case "rules_officer3":
                photoView.setImageResource(R.drawable.full_rules_officer3);
                break;
            case "rules_pawn1":
                photoView.setImageResource(R.drawable.full_rules_pawn1);
                break;
            case "rules_pawn2":
                photoView.setImageResource(R.drawable.full_rules_pawn2);
                break;
            case "rules_pawn3":
                photoView.setImageResource(R.drawable.full_rules_pawn3);
                break;
            case "rules_pawn4":
                photoView.setImageResource(R.drawable.full_rules_pawn4);
                break;
            case "rules_pawn5":
                photoView.setImageResource(R.drawable.full_rules_pawn5);
                break;
            case "rules_pawn6":
                photoView.setImageResource(R.drawable.full_rules_pawn6);
                break;
            case "rules_pawn7":
                photoView.setImageResource(R.drawable.full_rules_pawn7);
                break;
            case "rules_pawn8":
                photoView.setImageResource(R.drawable.full_rules_pawn8);
                break;
            case "rules_pawn9":
                photoView.setImageResource(R.drawable.full_rules_pawn9);
                break;
            case "rules_queen1":
                photoView.setImageResource(R.drawable.full_rules_queen1);
                break;
            case "rules_queen2":
                photoView.setImageResource(R.drawable.full_rules_queen2);
                break;
            case "rules_rook1":
                photoView.setImageResource(R.drawable.full_rules_rook1);
                break;
            case "rules_rook2":
                photoView.setImageResource(R.drawable.full_rules_rook2);
                break;
            case "rules_rook3":
                photoView.setImageResource(R.drawable.full_rules_rook3);
                break;
        }
    }
}
