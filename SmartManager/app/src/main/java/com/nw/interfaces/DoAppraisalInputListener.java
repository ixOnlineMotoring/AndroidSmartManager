package com.nw.interfaces;

public interface DoAppraisalInputListener
{
	/**
	 * @param isName this will indicate is add textchange is applied to title.
	 * @param position position on which textChange is applied.
	 * @param message text value to be passed.
	 */
	public void onButtonClicked(boolean isName,int position, String message);
}
