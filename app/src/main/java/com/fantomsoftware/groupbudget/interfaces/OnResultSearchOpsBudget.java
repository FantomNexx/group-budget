package com.fantomsoftware.groupbudget.interfaces;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationBudget;

public interface OnResultSearchOpsBudget{
   void OnResult( SparseArray<OperationBudget> ops_filtered);
}//OnResultSearchOpsBudget