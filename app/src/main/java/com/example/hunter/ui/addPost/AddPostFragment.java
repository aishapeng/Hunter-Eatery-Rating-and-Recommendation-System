package com.example.hunter.ui.addPost;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.bumptech.glide.Glide;
import com.example.hunter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser fbUser;
    private String imageEncoded;
    private ArrayList<String> imagesEncodedList;
    ImageAdapter myImageAdapter;


    private static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int RC_IMAGE_GALLERY = 2;

    private AddPostViewModel addPostViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),"perm",Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Toast.makeText(getActivity(),"here",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, RC_IMAGE_GALLERY);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addPostViewModel =
                ViewModelProviders.of(this).get(AddPostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addpost, container, false);
        Button post = root.findViewById(R.id.post);
        EditText location = root.findViewById(R.id.location);
        GridView gridView=root.findViewById(R.id.gridView);
        EditText experience=root.findViewById(R.id.experience);
        RatingBar ratingBar=root.findViewById(R.id.ratingBar);


        myImageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(myImageAdapter);


        addPostViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
//                startActivityForResult(intent, RC_IMAGE_GALLERY);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            imagesEncodedList = new ArrayList<String>();
            if(data.getData()!=null){

                Uri mImageUri=data.getData();

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(mImageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded  = cursor.getString(columnIndex);
                cursor.close();

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded  = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        myImageAdapter.add(imageEncoded);
//                        imagesEncodedList.add(uri);
                        cursor.close();

                    }
                    Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());

                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }
    } catch (Exception e) {
        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                .show();
    }
//            Uri uri = data.getData();
//            Intent intent=new Intent(this, AddPostFragment.class);
//            intent.putExtra("uri",uri);
//            startActivity(intent);

//            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//            StorageReference imagesRef = storageRef.child("images");
//            StorageReference userRef = imagesRef.child(fbUser.getUid());
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String filename = fbUser.getUid() + "_" + timeStamp;
//            StorageReference fileRef = userRef.child(filename);
//
//            UploadTask uploadTask = fileRef.putFile(uri);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Toast.makeText(FeedActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//                    Toast.makeText(FeedActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();
//
//                    // save image to database
//                    String key = database.child("images").push().getKey();
//                    Image image = new Image(key, fbUser.getUid(), downloadUrl.toString());
//                    database.child("images").child(key).setValue(image);
//                }
//            });
        }
    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        ArrayList<String> itemList = new ArrayList<String>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        void add(String path){
            itemList.add(path);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(370, 370));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(1, 0, 1, 0);
            } else {
                imageView = (ImageView) convertView;

            }


            Glide.with(getActivity()).load(imagesEncodedList.get(position)).into(imageView);
            notifyDataSetChanged();

            return imageView;
        }

    }

}

