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

/**
 * Pixiv における画像データをラップするクラス．
 * <p>
 * このオブジェクトが画像そのものを表すわけではありません．
 * 実際の画像 URL は，getImageURL から取得できます．
 * </p>
 *
 * @since 0.1
 */
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

	/**
	 * イラスト ID を取得する．
	 *
	 * @return イラスト ID
	 */
	public int getId(){
		return this.id;
	}

	/**
	 * 作者の ID を取得する．
	 *
	 * @return このイラストの作者 ID
	 */
	public int getAuthorId() {
		return authorId;
	}

	/**
	 * イラストの拡張子を取得する．
	 *
	 * @return このイラストの拡張子
	 */
	public String getExt() {
		return ext;
	}

	/**
	 * タイトルを取得する．
	 *
	 * @return このイラストのタイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * サーバ名を取得する．
	 *
	 * @return このイラストが保管されているサーバ名
	 */
	public String getServer() {
		return server;
	}

	/**
	 * 作者名を取得する．
	 *
	 * @return このイラストの作者
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * サムネイル画像の URL を取得する．
	 *
	 * @return サムネイル画像の URL
	 */
	public URL getThumbURL() {
		return thumbURL;
	}

	/**
	 * モバイル用画像の URL を取得する．
	 *
	 * @return モバイル用画像の URL
	 */
	public URL getMobileURL() {
		return mobileURL;
	}

	/**
	 * 投稿日を取得する．
	 *
	 * @return このイラストの投稿日
	 */
	public String getDate() {
		return date;
	}

	/**
	 * このイラストに付加されたタグを取得する．
	 *
	 * @return このイラストに付けられたタグ
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * 制作ツール名を取得する．
	 *
	 * @return 制作ツール名
	 */
	public String getTool() {
		return tool;
	}

	/**
	 * 評価点を取得する．
	 *
	 * @return 評価点
	 */
	public int getFeedback() {
		return feedback;
	}

	/**
	 * 総合点を取得する
	 *
	 * @return 総合点
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * 閲覧数を取得する．
	 *
	 * @return 閲覧数
	 */
	public int getViews() {
		return views;
	}

	/**
	 * 作者コメントを取得する．
	 *
	 * @return 作者コメント
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * 画像ページの URL を取得する．
	 *
	 * @return 画像ページの URL
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * 画像の URL を取得する．
	 *
	 * @return 画像の URL
	 */
	public URL getImageURL() {
		return imageURL;
	}

	//----------------------------------------------------------------------------

	/**
	 * このイラストの作者を取得する．
	 *
	 * @return このイラストの作者を表す User オブジェクト
	 */
	public User getAuthor(){
		return this.api.findUser(this.getAuthorId(), this.getAuthorName());
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
		ret.append(", title: ");
		ret.append(this.getTitle());
		ret.append(", author name: ");
		ret.append(this.getAuthorName());
		ret.append(", data: ");
		ret.append(this.getDate());
		ret.append("]");

		return ret.toString();

	}

	/* (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return Integer.valueOf(this.id).hashCode();

	}

	/* (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {

		if(obj instanceof Image){

			final Image that = (Image)obj;
			return this.getId() == that.getId();

		}

		return super.equals(obj);
	}


}
