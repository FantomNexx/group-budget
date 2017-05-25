package com.fantomsoftware.groupbudget.interfaces;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationBudget;

public interface OnResultStats{
   void OnResult( SparseArray<OperationBudget> data );
}//OnResultStats