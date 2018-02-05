package com.nw.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartmanager.android.R;

public class SpecialFragment extends BaseFragement implements OnClickListener{

	Button btnCreateSpecials, btnListActiveSpecials, btnExpiredSpecials,btnPublishSpecials;
	CreateSpecialsFragment createSpecialsFragment;
	ActiveSpecialFragment activeSpecialFragment;
	ExpiredSpecialFragment expiredSpecialFragment;
	PublishSpecialFragment publishSpecialFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view=inflater.inflate(R.layout.fragment_specials, container,false);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setHasOptionsMenu(true);
		initialise(view);
		return view;
	}
	
	private void initialise(View view){
		btnCreateSpecials= (Button) view.findViewById(R.id.btnCreateSpecials);
		btnListActiveSpecials= (Button) view.findViewById(R.id.btnListActiveSpecials);
		btnExpiredSpecials= (Button) view.findViewById(R.id.btnListExpiredSpecials);
		btnPublishSpecials= (Button) view.findViewById(R.id.btnListPublishSpecials);
		btnCreateSpecials.setOnClickListener(this);
		btnListActiveSpecials.setOnClickListener(this);
		btnPublishSpecials.setOnClickListener(this);
		btnExpiredSpecials.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.btnCreateSpecials:
			activeSpecialFragment=null;
			expiredSpecialFragment=null;
			publishSpecialFragment =null;
			createSpecialsFragment= new CreateSpecialsFragment();
			getFragmentManager().beginTransaction().replace(this.getId(),createSpecialsFragment).addToBackStack("special_fragment").commit();
			break;
			
		case R.id.btnListActiveSpecials:
			createSpecialsFragment=null;
			expiredSpecialFragment=null;
			publishSpecialFragment =null;
			activeSpecialFragment= new ActiveSpecialFragment();
			getFragmentManager().beginTransaction().replace(this.getId(),activeSpecialFragment).addToBackStack("special_fragment").commit();
			break;
			
		case R.id.btnListExpiredSpecials:
			activeSpecialFragment=null;
			createSpecialsFragment=null;
			publishSpecialFragment =null;
			expiredSpecialFragment= new ExpiredSpecialFragment();
			getFragmentManager().beginTransaction().replace(this.getId(),expiredSpecialFragment).addToBackStack("special_fragment").commit();
			break;
			
		case R.id.btnListPublishSpecials:
			activeSpecialFragment=null;
			createSpecialsFragment=null;
			publishSpecialFragment= new PublishSpecialFragment();
			getFragmentManager().beginTransaction().replace(this.getId(),publishSpecialFragment).addToBackStack("special_fragment").commit();
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(createSpecialsFragment!=null)
			createSpecialsFragment.onActivityResult(requestCode, resultCode, data);
		if(activeSpecialFragment!=null)
			activeSpecialFragment.onActivityResult(requestCode, resultCode, data);
		if(expiredSpecialFragment!=null)
			expiredSpecialFragment.onActivityResult(requestCode, resultCode, data);
		if(publishSpecialFragment!=null)
			publishSpecialFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		showActionBar("Specials");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*if(!ShowcaseSessions.isSessionAvailable(getActivity(), SpecialFragment.class.getSimpleName())){
			ArrayList<TargetView> viewList= new ArrayList<TargetView>();
			viewList.add(new TargetView(btnCreateSpecials, ShowCaseType.Left,getString(R.string.tap_here_to_create_specials)));
			ShowcaseLayout showcaseView = new ShowcaseLayout(getActivity());
			showcaseView.setShowcaseView(viewList);
			showcaseView.setClickable(true);
			((ViewGroup)getActivity().getWindow().getDecorView()).addView(showcaseView);
			ShowcaseSessions.saveSession(getActivity(), SpecialFragment.class.getSimpleName());
		}*/
	}
	
}
