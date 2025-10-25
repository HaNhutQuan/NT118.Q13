package com.example.lab03;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText edtmssv, edthoten, edtlop;
    Button btninsert, btndelete, btnupdate, btnquery;
    ListView lv;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtmssv = findViewById(R.id.edtmssv);
        edthoten = findViewById(R.id.edthoten);
        edtlop = findViewById(R.id.edtlop);
        btninsert = findViewById(R.id.btninsert);
        btndelete = findViewById(R.id.btndelete);
        btnupdate = findViewById(R.id.btnupdate);
        btnquery = findViewById(R.id.btnquery);
        lv = findViewById(R.id.lv);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        lv.setAdapter(myadapter);
        db = new DatabaseHelper(this);

        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mssv = edtmssv.getText().toString().trim();
                String hoten = edthoten.getText().toString().trim();
                String malop = edtlop.getText().toString().trim();
                if (mssv.isEmpty()) { Toast.makeText(MainActivity.this, "Nhập MSSV", Toast.LENGTH_SHORT).show(); return; }
                long r = db.addStudent(new Student(mssv, hoten, malop));
                if (r == -1) Toast.makeText(MainActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "Đã thêm", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mssv = edtmssv.getText().toString().trim();
                if (mssv.isEmpty()) { Toast.makeText(MainActivity.this, "Nhập MSSV để xóa", Toast.LENGTH_SHORT).show(); return; }
                int n = db.deleteStudent(mssv);
                if (n == 0) Toast.makeText(MainActivity.this, "Không có bản ghi để xóa", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "Đã xóa " + n, Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mssv = edtmssv.getText().toString().trim();
                String hoten = edthoten.getText().toString().trim();
                String malop = edtlop.getText().toString().trim();
                if (mssv.isEmpty()) { Toast.makeText(MainActivity.this, "Nhập MSSV để cập nhật", Toast.LENGTH_SHORT).show(); return; }
                int n = db.updateStudent(new Student(mssv, hoten, malop));
                if (n == 0) Toast.makeText(MainActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }
        });

        btnquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mylist.get(position);
                String[] parts = item.split(" - ");
                if (parts.length >= 3) {
                    edtmssv.setText(parts[0]);
                    edthoten.setText(parts[1]);
                    edtlop.setText(parts[2]);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = mylist.get(position);
                String[] parts = item.split(" - ");
                if (parts.length >= 1) {
                    String mssv = parts[0];
                    int n = db.deleteStudent(mssv);
                    if (n > 0) {
                        loadData();
                        Toast.makeText(MainActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        loadData();
    }

    void loadData() {
        mylist.clear();
        List<Student> all = db.getAllStudents();
        for (Student s : all) {
            mylist.add(s.mssv + " - " + s.hoten + " - " + s.malop);
        }
        myadapter.notifyDataSetChanged();
    }

    static class Student {
        String mssv, hoten, malop;
        Student() {}
        Student(String m, String h, String l) { mssv = m; hoten = h; malop = l; }
    }

    static class DatabaseHelper extends SQLiteOpenHelper {
        static final String NAME = "qlsinhvien.db";
        static final int VER = 1;
        static final String TABLE = "tbllop";
        DatabaseHelper(android.content.Context c) { super(c, NAME, null, VER); }
        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (mssv TEXT PRIMARY KEY, hoten TEXT, malop TEXT)");
        }
        @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }
        long addStudent(Student s) {
            try {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put("mssv", s.mssv);
                v.put("hoten", s.hoten);
                v.put("malop", s.malop);
                return db.insert(TABLE, null, v);
            } catch (Exception e) { return -1; }
        }
        int updateStudent(Student s) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put("hoten", s.hoten);
            v.put("malop", s.malop);
            return db.update(TABLE, v, "mssv = ?", new String[]{s.mssv});
        }
        int deleteStudent(String mssv) {
            SQLiteDatabase db = getWritableDatabase();
            return db.delete(TABLE, "mssv = ?", new String[]{mssv});
        }
        List<Student> getAllStudents() {
            List<Student> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.query(TABLE, null, null, null, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String m = c.getString(0);
                        String h = c.getString(1);
                        String l = c.getString(2);
                        list.add(new Student(m, h, l));
                    } while (c.moveToNext());
                }
                c.close();
            }
            return list;
        }
    }
}
