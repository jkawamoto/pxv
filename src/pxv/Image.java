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

public class Image {

	/** 親オブジェクト */
	private final PixivAPI api;

	/** イラストID */
	private final int id;
	/** ユーザーID */
	private final int authorId;
	/** 拡張子 */
	private final String ext;
	/** タイトル */
	private final String title;
	/** 画像サーバ */
	private final String server;
	/** 作者 */
	private final String authorName;
	/** サムネイル URL */
	private final URL thumbURL;
	/** モバイル画像 URL */
	private final URL mobileURL;
	/** 日付 */
	private final String date;
	/** タグ */
	private final String tags;
	/** 制作ツール */
	private final String tool;
	/** 評価点 */
	private final int feedback;
	/** 総合点 */
	private final int point;
	/** 閲覧数 */
	private final int views;
	/** 作者コメント */
	private final String comment;
	/** 画像ページの URL */
	private final URL url;
	/** 画像の URL */
	private final URL imageURL;

	Image(final PixivAPI api, final String[] data) throws IOException{
		assert data.length > 17;

		this.api = api;
		this.id = Integer.parseInt(data[0]);
		this.authorId = Integer.parseInt(data[1]);
		this.ext = data[2];
		this.title = data[3];
		this.server = data[4];
		this.authorName = data[5];
		this.thumbURL = new URL(data[6]);
		this.mobileURL = new URL(data[9]);
		this.date = data[12];
		this.tags = data[13];
		this.tool = data[14];
		this.feedback = Integer.parseInt(data[15]);
		this.point = Integer.parseInt(data[16]);
		this.views = Integer.parseInt(data[17]);
		this.comment = data[18];

		this.url = new URL(String.format("http://www.pixiv.net/member_illust.php?mode=medium&illust_id=%s", this.id));

		final String rawMobileURL = this.mobileURL.toString();
		this.imageURL = new URL(rawMobileURL.substring(0, rawMobileURL.lastIndexOf("/mobile/") + 1) + this.id + "." + this.ext);

	}

	public int getId(){
		return this.id;
	}

	public int getAuthorId() {
		return authorId;
	}

	public String getExt() {
		return ext;
	}

	public String getTitle() {
		return title;
	}

	public String getServer() {
		return server;
	}

	public String getAuthorName() {
		return authorName;
	}

	public URL getThumbURL() {
		return thumbURL;
	}

	public URL getMobileURL() {
		return mobileURL;
	}

	public String getDate() {
		return date;
	}

	public String getTags() {
		return tags;
	}

	public String getTool() {
		return tool;
	}

	public int getFeedback() {
		return feedback;
	}

	public int getPoint() {
		return point;
	}

	public int getViews() {
		return views;
	}

	public String getComment() {
		return comment;
	}

	public URL getUrl() {
		return url;
	}

	public URL getImageURL() {
		return imageURL;
	}

	//----------------------------------------------------------------------------
	public User getAuthor(){
		return this.api.findUser(this.getAuthorId(), this.getAuthorName());
	}

	//----------------------------------------------------------------------------
	@Override
	public String toString(){

		final StringBuilder ret = new StringBuilder();
		ret.append(this.getClass().getName());
		ret.append("[id: ");
		ret.append(this.getId());
		ret.append(", title: ");
		ret.append(this.getTitle());
		ret.append(", author name: ");
		ret.append(this.getAuthorName());
		ret.append(", data: ");
		ret.append(this.getDate());
		ret.append("]");

		return ret.toString();

	}


}
