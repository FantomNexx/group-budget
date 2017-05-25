package com.fantomsoftware.groupbudget.interfaces;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Currency;

public interface OnResultSearchCurrency{
   void OnResult(
      SparseArray<Currency> currencies_filtered,
      SparseArray<Currency> currencies_used_filtered );
}//OnResultSearchCurrency