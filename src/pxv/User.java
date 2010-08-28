/*
 *  Copyright (C) 2010 Junpei Kawamoto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package pxv;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class User {

	/** 親オブジェクト */
	private final PixivAPI api;

	/** ユーザ ID */
	private final int id;
	/** ユーザ名 */
	private final String name;
	/** モバイル画像のURL */
	private final URL mobileURL;
	/** アルファベットユーザ名 */
	private final String ename;

	//============================================================================
	//  Constructor
	//============================================================================
	User(final PixivAPI api, final String[] data) throws IOException{
		assert data.length >= 25;

		this.api = api;
		this.id = Integer.parseInt(data[1]);
		this.name = data[5];
		this.mobileURL = new URL(data[6]);
		this.ename = data[24];

	}

	//============================================================================
	//  Public methods
	//============================================================================
	//----------------------------------------------------------------------------
	// For getting this user's information
	//----------------------------------------------------------------------------
	/**
	 * ユーザ ID を取得する．
	 *
	 * @return このユーザの ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * ユーザ名を取得する．
	 *
	 * @return このユーザの名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * モバイルプロフィール画像の URL を取得する．
	 *
	 * @return モバイルプロフィール画像の URL
	 */
	public URL getMobileURL() {
		return mobileURL;
	}

	/**
	 * アルファベット表記のユーザ名を取得する．
	 *
	 * @return このユーザのアルファベット表記名
	 */
	public String getEname() {
		return ename;
	}

	//----------------------------------------------------------------------------
	//
	//----------------------------------------------------------------------------
	/**
	 * このユーザの投稿画像数を取得する．
	 *
	 * @return 投稿画像総数
	 */
	public int getImageSize(){
		return this.api.getImageSize(this);
	}

	public List<Image> getImages(final int size){
		return this.api.getImages(this, size);
	}

	/**
	 * MyPixiv に登録しているユーザ数を取得する．
	 *
	 * @return
	 */
	public int getMyPixivSize(){
		return this.api.getMyPixivSize(this);
	}

	public List<User> getMyPixivUsers(final int size){
		return this.api.getMyPixivUsers(this, size);
	}

	public int getBookmarkedUserSize(){
		return this.api.getBookmarkedUserSize(this);
	}

	public List<User> getBookmarkedUsers(final int size){
		return this.api.getBookmarkedUsers(this, size);
	}

	public int getBookmarkSize(){
		return this.api.getBookmarkSize(this);
	}

	public List<Image> getBookmarks(final int size){
		return this.api.getBookmarks(this, size);
	}

	//----------------------------------------------------------------------------

	/* (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){

		final StringBuilder ret = new StringBuilder();
		ret.append(this.getClass().getName());
		ret.append("[id: ");
		ret.append(this.getId());
		ret.append(", name: ");
		ret.append(this.getName());
		ret.append(", mobile url: ");
		ret.append(this.getMobileURL());
		ret.append(", ename: ");
		ret.append(this.getEname());
		ret.append("]");

		return ret.toString();

	}

}
