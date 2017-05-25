package com.fantomsoftware.groupbudget.adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class TabPagerAdapter extends FragmentPagerAdapter{
//---------------------------------------------------------------------------
private final List<Fragment> list_fragments       = new ArrayList<>();
private final List<String>   list_fragment_titles = new ArrayList<>();
//---------------------------------------------------------------------------


//----------------------------------------------------------------------------------
public TabPagerAdapter( android.support.v4.app.FragmentManager fragmentManager ){
  super( fragmentManager );
}//TabPagerAdapter
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public void addFragment( Fragment fragment, String title ){
  list_fragments.add( fragment );
  list_fragment_titles.add( title );
}//addFragment
//---------------------------------------------------------------------------
@Override
public Fragment getItem( int position ){
  return list_fragments.get( position );
}//getItem
//---------------------------------------------------------------------------
@Override
public int getCount(){
  if( list_fragments == null ){
    return 0;
  }//if

  return list_fragments.size();
}//getCount
//---------------------------------------------------------------------------
@Override
public CharSequence getPageTitle( int position ){
  return list_fragment_titles.get( position );
}//getPageTitle
//---------------------------------------------------------------------------

}//TabPagerAdapter

