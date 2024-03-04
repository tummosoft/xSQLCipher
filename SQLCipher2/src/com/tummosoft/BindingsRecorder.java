package com.tummosoft;

import android.util.SparseArray;
import androidx.sqlite.db.SupportSQLiteProgram;

class BindingsRecorder implements SupportSQLiteProgram {
  private SparseArray<Object> bindings=new SparseArray<>();

  @Override
  public void bindNull(int index) {
    bindings.put(index, null);
  }

  @Override
  public void bindLong(int index, long value) {
    bindings.put(index, value);
  }

  @Override
  public void bindDouble(int index, double value) {
    bindings.put(index, value);
  }

  @Override
  public void bindString(int index, String value) {
    bindings.put(index, value);
  }

  @Override
  public void bindBlob(int index, byte[] value) {
    bindings.put(index, value);
  }

  @Override
  public void clearBindings() {
    bindings.clear();
  }

  @Override
  public void close() {
    clearBindings();
  }

  String[] getBindings() {
    final String[] result=new String[bindings.size()];

    for (int i=0;i<bindings.size();i++) {
      int key=bindings.keyAt(i);
      Object binding=bindings.get(key);

      if (binding!=null) {
        result[i]=bindings.get(key).toString();
      }
      else {
        result[i]=""; // SQLCipher does not like null binding values
      }
    }

    return(result);
  }
}