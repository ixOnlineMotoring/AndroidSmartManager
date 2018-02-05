package com.nw.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nw.interfaces.DialogListener;
import com.nw.model.VehicleDetails;
import com.nw.widget.CustomDialogManager;
import com.smartmanager.android.R;

public class Exterior_ReconditioningFragment extends BaseFragement implements OnClickListener
{
	LinearLayout llayoutBumper, llayoutHeadLightLF, llayoutGrill, llayoutHeadLightRF, llayoutFenderLF, llayoutBonet, llayoutFenderRF, llayoutTyreLF, llayoutRimLF, llayoutFenderLF1, llayoutBonet1,
			llayoutFenderRF1, llayoutTyreRF, llayoutRimRF, llayoutDoorLF, llayoutWindowLF, llayoutWipers, llayoutWindScreenFront, llayoutWindowRF, llayoutDoorRF, llayoutDoorLF1, llayoutWindowLF1,
			llayoutSunroof, llayoutWindowRF1, llayoutDoorRF1, llayoutDoorLR, llayoutWindowLR, llayoutRoof, llayoutWindowRR, llayoutDoorRR, llayoutTyreLR, llayoutDoorLR1, llayoutWindowLR1,
			llayoutWindScreenRear, llayoutWindowRR1, llayoutDoorRR1, llayoutTyreRR, llayoutTyreLR1, llayoutRimLR, llayoutTyreLR2, llayoutFenderLR, llayoutTools, llayoutBoot, llayoutSpareWheel,
			llayoutFenderRR, llayoutTyreRR1, llayoutRimRR, llayoutTyreRR2, llayoutTyreLR3, llayoutFenderLR1, llayoutBoot1, llayoutSpareWheel1, llayoutFenderRR1, llayoutTyreRR3, llayoutFenderLR2,
			llayoutBoot2, llayoutSpareWheel2, llayoutFenderRR2, llayoutTaillightLR, llayoutTaillightRR, llayoutBumperRear, llayoutExhaus, llayoutBumperRear1;

	ReconItemAreaFragment reconItemAreaFragment;
	VehicleDetails vehicleDetails;

	int selected_item = 0;
	private String str_title ="";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_exterior_reconditioning, container, false);
		setHasOptionsMenu(true);
		if (getArguments() != null)
		{
			vehicleDetails = getArguments().getParcelable("vehicleDetails");
		}
		initialise(view);
		return view;
	}

	private void initialise(View view)
	{
		llayoutBumper = (LinearLayout) view.findViewById(R.id.llayoutBumper);
		llayoutHeadLightLF = (LinearLayout) view.findViewById(R.id.llayoutHeadLightLF);
		llayoutGrill = (LinearLayout) view.findViewById(R.id.llayoutGrill);
		llayoutHeadLightRF = (LinearLayout) view.findViewById(R.id.llayoutHeadLightRF);

		llayoutFenderLF = (LinearLayout) view.findViewById(R.id.llayoutFenderLF);
		llayoutBonet = (LinearLayout) view.findViewById(R.id.llayoutBonet);
		llayoutFenderRF = (LinearLayout) view.findViewById(R.id.llayoutFenderRF);
		llayoutTyreLF = (LinearLayout) view.findViewById(R.id.llayoutTyreLF);
		llayoutRimLF = (LinearLayout) view.findViewById(R.id.llayoutRimLF);
		llayoutFenderLF1 = (LinearLayout) view.findViewById(R.id.llayoutFenderLF1);
		llayoutBonet1 = (LinearLayout) view.findViewById(R.id.llayoutBonet1);
		llayoutFenderRF1 = (LinearLayout) view.findViewById(R.id.llayoutFenderRF1);
		llayoutTyreRF = (LinearLayout) view.findViewById(R.id.llayoutTyreRF);
		llayoutRimRF = (LinearLayout) view.findViewById(R.id.llayoutRimRF);

		llayoutDoorLF = (LinearLayout) view.findViewById(R.id.llayoutDoorLF);
		llayoutWindowLF = (LinearLayout) view.findViewById(R.id.llayoutWindowLF);
		llayoutWipers = (LinearLayout) view.findViewById(R.id.llayoutWipers);
		llayoutWindScreenFront = (LinearLayout) view.findViewById(R.id.llayoutWindScreenFront);
		llayoutWindowRF = (LinearLayout) view.findViewById(R.id.llayoutWindowRF);
		llayoutDoorRF = (LinearLayout) view.findViewById(R.id.llayoutDoorRF);
		llayoutDoorLF1 = (LinearLayout) view.findViewById(R.id.llayoutDoorLF1);
		llayoutWindowLF1 = (LinearLayout) view.findViewById(R.id.llayoutWindowLF1);
		llayoutSunroof = (LinearLayout) view.findViewById(R.id.llayoutSunroof);
		llayoutWindowRF1 = (LinearLayout) view.findViewById(R.id.llayoutWindowRF1);
		llayoutDoorRF1 = (LinearLayout) view.findViewById(R.id.llayoutDoorRF1);
		llayoutDoorLR = (LinearLayout) view.findViewById(R.id.llayoutDoorLR);

		llayoutWindowLR = (LinearLayout) view.findViewById(R.id.llayoutWindowLR);
		llayoutRoof = (LinearLayout) view.findViewById(R.id.llayoutRoof);
		llayoutWindowRR = (LinearLayout) view.findViewById(R.id.llayoutWindowRR);
		llayoutDoorRR = (LinearLayout) view.findViewById(R.id.llayoutDoorRR);

		llayoutTyreLR = (LinearLayout) view.findViewById(R.id.llayoutTyreLR);
		llayoutDoorLR1 = (LinearLayout) view.findViewById(R.id.llayoutDoorLR1);
		llayoutWindowLR1 = (LinearLayout) view.findViewById(R.id.llayoutWindowLR1);
		llayoutWindScreenRear = (LinearLayout) view.findViewById(R.id.llayoutWindScreenRear);
		llayoutWindowRR1 = (LinearLayout) view.findViewById(R.id.llayoutWindowRR1);
		llayoutDoorRR1 = (LinearLayout) view.findViewById(R.id.llayoutDoorRR1);
		llayoutTyreRR = (LinearLayout) view.findViewById(R.id.llayoutTyreRR);

		llayoutTyreLR1 = (LinearLayout) view.findViewById(R.id.llayoutTyreLR1);
		llayoutRimLR = (LinearLayout) view.findViewById(R.id.llayoutRimLR);
		llayoutTyreLR2 = (LinearLayout) view.findViewById(R.id.llayoutTyreLR2);
		llayoutFenderLR = (LinearLayout) view.findViewById(R.id.llayoutFenderLR);
		llayoutTools = (LinearLayout) view.findViewById(R.id.llayoutTools);
		llayoutBoot = (LinearLayout) view.findViewById(R.id.llayoutBoot);
		llayoutSpareWheel = (LinearLayout) view.findViewById(R.id.llayoutSpareWheel);
		llayoutFenderRR = (LinearLayout) view.findViewById(R.id.llayoutFenderRR);
		llayoutTyreRR1 = (LinearLayout) view.findViewById(R.id.llayoutTyreRR1);
		llayoutRimRR = (LinearLayout) view.findViewById(R.id.llayoutRimRR);
		llayoutTyreRR2 = (LinearLayout) view.findViewById(R.id.llayoutTyreRR2);

		llayoutTyreLR3 = (LinearLayout) view.findViewById(R.id.llayoutTyreLR3);
		llayoutFenderLR1 = (LinearLayout) view.findViewById(R.id.llayoutFenderLR1);
		llayoutBoot1 = (LinearLayout) view.findViewById(R.id.llayoutBoot1);
		llayoutSpareWheel1 = (LinearLayout) view.findViewById(R.id.llayoutSpareWheel1);
		llayoutFenderRR1 = (LinearLayout) view.findViewById(R.id.llayoutFenderRR1);
		llayoutTyreRR3 = (LinearLayout) view.findViewById(R.id.llayoutTyreRR3);

		llayoutFenderLR2 = (LinearLayout) view.findViewById(R.id.llayoutFenderLR2);
		llayoutBoot2 = (LinearLayout) view.findViewById(R.id.llayoutBoot2);
		llayoutSpareWheel2 = (LinearLayout) view.findViewById(R.id.llayoutSpareWheel2);
		llayoutFenderRR2 = (LinearLayout) view.findViewById(R.id.llayoutFenderRR2);

		llayoutTaillightLR = (LinearLayout) view.findViewById(R.id.llayoutTaillightLR);
		llayoutTaillightRR = (LinearLayout) view.findViewById(R.id.llayoutTaillightRR);
		llayoutBumperRear = (LinearLayout) view.findViewById(R.id.llayoutBumperRear);
		llayoutExhaus = (LinearLayout) view.findViewById(R.id.llayoutExhaus);
		llayoutBumperRear1 = (LinearLayout) view.findViewById(R.id.llayoutBumperRear1);

		llayoutBumper.setOnClickListener(this);
		llayoutHeadLightLF.setOnClickListener(this);
		llayoutGrill.setOnClickListener(this);
		llayoutHeadLightRF.setOnClickListener(this);

		llayoutFenderLF.setOnClickListener(this);
		llayoutBonet.setOnClickListener(this);
		llayoutFenderRF.setOnClickListener(this);
		llayoutTyreLF.setOnClickListener(this);
		llayoutRimLF.setOnClickListener(this);
		llayoutFenderLF1.setOnClickListener(this);
		llayoutBonet1.setOnClickListener(this);
		llayoutFenderRF1.setOnClickListener(this);
		llayoutTyreRF.setOnClickListener(this);
		llayoutRimRF.setOnClickListener(this);

		llayoutDoorLF.setOnClickListener(this);
		llayoutWindowLF.setOnClickListener(this);
		llayoutWipers.setOnClickListener(this);
		llayoutWindScreenFront.setOnClickListener(this);
		llayoutWindowRF.setOnClickListener(this);
		llayoutDoorRF.setOnClickListener(this);
		llayoutDoorLF1.setOnClickListener(this);
		llayoutWindowLF1.setOnClickListener(this);
		llayoutSunroof.setOnClickListener(this);
		llayoutWindowRF1.setOnClickListener(this);
		llayoutDoorRF1.setOnClickListener(this);
		llayoutDoorLR.setOnClickListener(this);

		llayoutWindowLR.setOnClickListener(this);
		llayoutRoof.setOnClickListener(this);
		llayoutWindowRR.setOnClickListener(this);
		llayoutDoorRR.setOnClickListener(this);

		llayoutTyreLR.setOnClickListener(this);
		llayoutDoorLR1.setOnClickListener(this);
		llayoutWindowLR1.setOnClickListener(this);
		llayoutWindScreenRear.setOnClickListener(this);
		llayoutWindowRR1.setOnClickListener(this);
		llayoutDoorRR1.setOnClickListener(this);
		llayoutTyreRR.setOnClickListener(this);

		llayoutTyreLR1.setOnClickListener(this);
		llayoutRimLR.setOnClickListener(this);
		llayoutTyreLR2.setOnClickListener(this);
		llayoutFenderLR.setOnClickListener(this);
		llayoutTools.setOnClickListener(this);
		llayoutBoot.setOnClickListener(this);
		llayoutSpareWheel.setOnClickListener(this);
		llayoutFenderRR.setOnClickListener(this);
		llayoutTyreRR1.setOnClickListener(this);
		llayoutRimRR.setOnClickListener(this);
		llayoutTyreRR2.setOnClickListener(this);

		llayoutTyreLR3.setOnClickListener(this);
		llayoutFenderLR1.setOnClickListener(this);
		llayoutBoot1.setOnClickListener(this);
		llayoutSpareWheel1.setOnClickListener(this);
		llayoutFenderRR1.setOnClickListener(this);
		llayoutTyreRR3.setOnClickListener(this);

		llayoutFenderLR2.setOnClickListener(this);
		llayoutBoot2.setOnClickListener(this);
		llayoutSpareWheel2.setOnClickListener(this);
		llayoutFenderRR2.setOnClickListener(this);

		llayoutTaillightLR.setOnClickListener(this);
		llayoutTaillightRR.setOnClickListener(this);
		llayoutBumperRear.setOnClickListener(this);
		llayoutExhaus.setOnClickListener(this);
		llayoutBumperRear1.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)

	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				hideKeyboard();
				getActivity().getFragmentManager().popBackStack();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		showActionBar("Select Recon Item / Area");
		//getActivity().getActionBar().setSubtitle(null);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

			case R.id.llayoutBumper:
				showDialog("Bumper Front");
				selected_item = 1;
				break;
			case R.id.llayoutHeadLightLF:
				showDialog("Headlight LF");
				selected_item = 3;
				break;
			case R.id.llayoutGrill:
				showDialog("Gril");
				selected_item = 2;
				break;
			case R.id.llayoutHeadLightRF:
				showDialog("Headlight RF");
				selected_item = 4;
				break;
			case R.id.llayoutFenderLF:
				showDialog("Fender LF");
				selected_item = 5;
				break;
			case R.id.llayoutBonet:
				showDialog("Bonet");
				selected_item = 7;
				break;
			case R.id.llayoutFenderRF:
				showDialog("Fender RF");
				selected_item = 6;
				break;
			case R.id.llayoutTyreLF:
				showDialog("Tyre LF");
				selected_item = 8;
				break;
			case R.id.llayoutRimLF:
				showDialog("Rim LF");
				selected_item = 10;
				break;
			case R.id.llayoutFenderLF1:
				showDialog("Fender LF");
				selected_item = 5;
				break;
			case R.id.llayoutBonet1:
				showDialog("Bonet");
				selected_item = 7;
				break;
			case R.id.llayoutFenderRF1:
				showDialog("Fender RF");
				selected_item = 6;
				break;
			case R.id.llayoutTyreRF:
				showDialog("Tyre RF");
				selected_item = 9;
				break;
			case R.id.llayoutRimRF:
				showDialog("Rim RF");
				selected_item = 11;
				break;
			case R.id.llayoutDoorLF:
				showDialog("Door LF");
				selected_item = 14;
				break;
			case R.id.llayoutWindowLF:
				showDialog("WindowLF");
				selected_item = 16;
				break;
			case R.id.llayoutWipers:
				showDialog("Wipers");
				selected_item = 12;
				break;
			case R.id.llayoutWindScreenFront:
				showDialog("Windscreen Front");
				selected_item = 13;
				break;
			case R.id.llayoutWindowRF:
				showDialog("Window RF");
				selected_item = 17;
				break;
			case R.id.llayoutDoorRF:
				showDialog("Door RF");
				selected_item = 15;
				break;
			case R.id.llayoutDoorLF1:
				showDialog("Door LF");
				selected_item = 14;
				break;
			case R.id.llayoutWindowLF1:
				showDialog("Window LF");
				selected_item = 16;
				break;
			case R.id.llayoutSunroof:
				showDialog("Sunroof");
				selected_item = 18;
				break;
			case R.id.llayoutWindowRF1:
				showDialog("Window RF");
				selected_item = 17;
				break;
			case R.id.llayoutDoorRF1:
				showDialog("Door RF");
				selected_item = 15;
				break;
			case R.id.llayoutDoorLR:
				showDialog("Door LR");
				selected_item = 21;
				break;
			case R.id.llayoutWindowLR:
				showDialog("Window LR");
				selected_item = 19;
				break;
			case R.id.llayoutRoof:
				showDialog("Roof");
				selected_item = 23;
				break;
			case R.id.llayoutWindowRR:
				showDialog("Window RR");
				selected_item = 20;
				break;
			case R.id.llayoutDoorRR:
				showDialog("Door RR");
				selected_item = 22;
				break;
			case R.id.llayoutTyreLR:
				showDialog("Tyre LR");
				selected_item = 25;
				break;
			case R.id.llayoutDoorLR1:
				showDialog("Door LR");
				selected_item = 21;
				break;
			case R.id.llayoutWindowLR1:
				showDialog("Window LR");
				selected_item = 19;
				break;
			case R.id.llayoutWindScreenRear:
				showDialog("Windscreen Rear");
				selected_item = 24;
				break;
			case R.id.llayoutWindowRR1:
				showDialog("Window RR");
				selected_item = 20;
				break;
			case R.id.llayoutDoorRR1:
				showDialog("Door RR");
				selected_item = 22;
				break;
			case R.id.llayoutTyreRR:
				showDialog("Tyre RR");
				selected_item = 26;
				break;
			case R.id.llayoutTyreLR1:
				showDialog("Tyre LR");
				selected_item = 25;
				break;
			case R.id.llayoutRimLR:
				showDialog("Rim LR");
				selected_item = 27;
				break;
			case R.id.llayoutTyreLR2:
				showDialog("Tyre LR");
				selected_item = 25;
				break;
			case R.id.llayoutFenderLR:
				showDialog("Fender LR");
				selected_item = 32;
				break;
			case R.id.llayoutTools:
				showDialog("Tools");
				selected_item = 29;
				break;
			case R.id.llayoutBoot:
				showDialog("Boot");
				selected_item = 31;
				break;
			case R.id.llayoutSpareWheel:
				showDialog("Sparewheel");
				selected_item = 30;
				break;
			case R.id.llayoutFenderRR:
				showDialog("Fender RR");
				selected_item = 33;
				break;
			case R.id.llayoutTyreRR1:
				showDialog("Tyre RR");
				selected_item = 26;
				break;
			case R.id.llayoutRimRR:
				showDialog("Rim RR");
				selected_item = 28;
				break;
			case R.id.llayoutTyreRR2:
				showDialog("Tyre RR");
				selected_item = 26;
				break;
			case R.id.llayoutTyreLR3:
				showDialog("Tyre LR");
				selected_item = 25;
				break;
			case R.id.llayoutFenderLR1:
				showDialog("Fender LR");
				selected_item = 32;
				break;
			case R.id.llayoutBoot1:
				showDialog("Boot");
				selected_item = 31;
				break;
			case R.id.llayoutSpareWheel1:
				showDialog("Sparewheel");
				selected_item = 30;
				break;
			case R.id.llayoutFenderRR1:
				showDialog("Fender RR");
				selected_item = 33;
				break;
			case R.id.llayoutTyreRR3:
				showDialog("Tyre RR");
				selected_item = 26;
				break;
			case R.id.llayoutFenderLR2:
				showDialog("Fender LR");
				selected_item = 32;
				break;
			case R.id.llayoutBoot2:
				showDialog("Boot");
				selected_item = 31;
				break;
			case R.id.llayoutSpareWheel2:
				showDialog("Sparewheel");
				selected_item = 30;
				break;
			case R.id.llayoutFenderRR2:
				showDialog("Fender RR");
				selected_item = 33;
				break;
			case R.id.llayoutTaillightLR:
				showDialog("Taillight LR");
				selected_item = 34;
				break;
			case R.id.llayoutTaillightRR:
				showDialog("Taillight RR");
				selected_item = 35;
				break;
			case R.id.llayoutBumperRear:
				showDialog("Bumper Rear");
				selected_item = 36;
				break;
			case R.id.llayoutExhaus:
				showDialog("Exhaust");
				selected_item = 37;
				break;
			case R.id.llayoutBumperRear1:
				showDialog("Bumper Rear");
				selected_item = 36;
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(reconItemAreaFragment != null)
		reconItemAreaFragment.onActivityResult(requestCode, resultCode, data);
	}

	private void showDialog(String message)
	{
		str_title = message;
		CustomDialogManager.showOkCancelDialog(getActivity(), "You selected "+message+"."+" Is this correct ?", "Yes", "No", new DialogListener()
		{
			@Override
			public void onButtonClicked(int type)
			{
				if (type == DialogInterface.BUTTON_POSITIVE)
				{
					reconItemAreaFragment = new ReconItemAreaFragment();
					Bundle bundle = new Bundle();
					bundle.putString("selected_item", ""+selected_item);
					bundle.putString("selected_str_title", ""+str_title);
					bundle.putParcelable("vehicleDetails", getArguments().getParcelable("vehicleDetails"));
					reconItemAreaFragment.setArguments(bundle);

					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.Container, reconItemAreaFragment).addToBackStack(null);
					ft.commit();
				}
			}
		});
	}
}
