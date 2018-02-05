package com.sonyericsson.util;


import android.net.wifi.WifiConfiguration;
import android.os.Environment;

import java.io.File;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory
{

	@Override
	public File getAlbumStorageDir(String albumName)
	{

		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
	}
}