package com.nw.showcase;
import android.view.View;

import com.nw.showcase.ShowcaseLayout.ShowCaseType;
public class TargetView {
	View view;
	ShowCaseType showcase_type;
	String message;
	public TargetView() {
		// TODO Auto-generated constructor stub
	}
	public TargetView(View view, ShowCaseType showcase_type, String message) {
		super();
		this.view = view;
		this.showcase_type = showcase_type;
		this.message = message;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public ShowCaseType getShowcase_type() {
		return showcase_type;
	}
	public void setShowcase_type(ShowCaseType showcase_type) {
		this.showcase_type = showcase_type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
