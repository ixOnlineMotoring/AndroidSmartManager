package com.sonyericsson.util;

import java.io.File;


public abstract class AlbumStorageDirFactory
{
	public abstract File getAlbumStorageDir(String albumName);
}
