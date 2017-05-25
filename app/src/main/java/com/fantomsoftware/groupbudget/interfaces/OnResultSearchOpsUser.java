package com.fantomsoftware.groupbudget.interfaces;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationUser;

public interface OnResultSearchOpsUser{
   void OnResult( SparseArray<OperationUser> ops_filtered );
}//OnResultSearchOpsUser