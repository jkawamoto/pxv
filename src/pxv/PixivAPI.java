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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pxv.CSVParser.Handler;

/**
 * Pixiv API．
 *
 * @since 0.1
 */
public class PixivAPI {

	private final URL base;

	private String session = Zero;

	//============================================================================
	//  Constants
	//============================================================================
	private static final String BaseURL = "http://iphone.pxv.jp/iphone/";
	private static final String SessionID = "PHPSESSID";

	private enum Type{
		new_illust, mypixiv_new_illust, bookmark_user_new_illust, ranking, search, search_user, bookmark, bookmark_user_all, mypixiv_all, member_illust
	}

	private static final String DummyParameter = "dummy=0";
	private static final String Daily = "mode=day";
	private static final String Weekly = "mode=week";
	private static final String Monthly = "mode=month";

	private static final String IDParamTemplate = "id=%d";

	private static final String UTF8 = "UTF-8";
	private static final String Zero = "0";

	//============================================================================
	//  Constructors
	//============================================================================
	/**
	 * PixivAPI インスタンスを作成する．
	 *
	 * @throws IOException I/O エラーが発生した場合．
	 */
	public PixivAPI() throws IOException{

		this.base = new URL(BaseURL);

	}

	//============================================================================
	//  Public methods
	//============================================================================
	//----------------------------------------------------------------------------
	//  APIs for user's account
	//----------------------------------------------------------------------------
	/**
	 * ログインする．
	 *
	 * @param id ユーザ ID
	 * @param password パスワード
	 * @return ログインに成功した場合 true
	 */
	public boolean login(final String id, final String password){

		try {

			final URL url = new URL(base, String.format("login.php?mode=login&pixiv_id=%s&pass=%s&skip=0", id, password));
			final HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.connect();

			if(con.getResponseCode() == 200){

				final URL res = con.getURL();
				this.session = this.parseParameters(res.getQuery()).get(SessionID);

			}

		} catch (final IOException e) {

			// TODO 自動生成された catch ブロック
			e.printStackTrace();

		}

		return this.logined();

	}

	/**
	 * ログインしているか調べる．
	 *
	 * @return ログインしている場合 true
	 */
	public boolean logined(){

		return this.session != Zero;

	}

	/**
	 * システムの稼働状態を調べる．
	 *
	 * @return システムが稼働している場合 true
	 */
	public boolean status(){

		try {

			final URL url = new URL(this.base, "maintenance.php?software-version=1.0");
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();

			return con.getResponseCode() == 200;

		} catch (final IOException e) {

			e.printStackTrace();

		}

		return false;

	}

	/**
	 * ログインユーザのプロフィールを取得する．
	 *
	 * @return プロフィール
	 */
	public String profile(){

		final StringBuffer ret = new StringBuffer();
		try {

			final URL url = new URL(this.base, String.format("profile.php?dummy=0&%s=%s", SessionID, this.session));
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();

			if(con.getResponseCode() == 200){

				final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				for(String buf; (buf = in.readLine()) != null;){

					ret.append(buf);
					ret.append("\n");

				}
				in.close();

			}

		} catch (final IOException e) {

			e.printStackTrace();

		}

		return ret.toString();

	}

	//----------------------------------------------------------------------------
	//  APIs for new images
	//----------------------------------------------------------------------------
	public int getNewImageSize(){
		return this.getSize(Type.new_illust, DummyParameter);
	}

	public List<Image> getNewImages(final int page){
		return this.getImages(Type.new_illust, DummyParameter, page);
	}

	public int getMyPixivNewImageSize(){
		return this.getSize(Type.mypixiv_new_illust, DummyParameter);
	}

	public List<Image> getMyPixivNewImages(final int page){
		return this.getImages(Type.mypixiv_new_illust, DummyParameter, page);
	}

	public int getBookmarkedUserNewImageSize(){
		return this.getSize(Type.bookmark_user_new_illust, DummyParameter);
	}

	public List<Image> getBookmarkedUserNewImages(final int page){
		return this.getImages(Type.bookmark_user_new_illust, DummyParameter, page);
	}

	public int getDailyRankingImageSize(){
		return this.getSize(Type.ranking, Daily);
	}

	public List<Image> getDailyRankingImages(final int page){
		return this.getImages(Type.ranking, Daily, page);
	}

	public int getWeeklyRankingImageSize(){
		return this.getSize(Type.ranking, Weekly);
	}

	public List<Image> getWeeklyRankingImages(final int page){
		return this.getImages(Type.ranking, Weekly, page);
	}

	public int getMonthlyRankingImageSize(){
		return this.getSize(Type.ranking, Monthly);
	}

	public List<Image> getMonthlyRankingImages(final int page){
		return this.getImages(Type.ranking, Monthly, page);
	}


	//----------------------------------------------------------------------------
	//  APIs for search
	//----------------------------------------------------------------------------
	public List<Image> findImagesByTag(final String keyword, final int size) throws IOException{

		final String param = String.format("s_mode=s_tag&word=%s", URLEncoder.encode(keyword , UTF8));
		return this.findImages(param, size);
	}

	public List<Image> findImagesByTitle(final String keyword, final int size) throws IOException{

		final String param = String.format("s_mode=s_tc&word=%s", URLEncoder.encode(keyword , UTF8));
		return this.findImages(param, size);

	}

	public List<User> findUsers(final String name, final int size) throws IOException{

		final List<User> ret = new ArrayList<User>();
		final String param = String.format("nick=%s", URLEncoder.encode(name , UTF8));
		for(int i = 0; true; ++i){

			final List<User> sub = this.getUsers(Type.search, param, i);
			if(sub.size() == 0){

				break;

			}

			ret.addAll(sub);

		}

		return ret;

	}

	public User findUser(final int id, final String name){

		final List<User> ret = new ArrayList<User>();
		try{

			for(int i = 0; ret.size() == 0 && i < 100; ++i){

				final URL url = new URL(this.base, String.format("%s.php?nick=%s&%s=%s&p=%d", Type.search_user, URLEncoder.encode(name, UTF8), SessionID, this.session, i));
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();

				if(con.getResponseCode() == 200){

					final Reader in = new InputStreamReader(con.getInputStream());
					CSVParser.parse(in, new Handler(){

						@Override
						public void update(final String[] data) {

							try {

								final User u = new User(PixivAPI.this, data);
								if(u.getId() == id){

									ret.add(u);

								}

							} catch (IOException e) {

								// TODO 自動生成された catch ブロック
								e.printStackTrace();

							}

						}

					});
					in.close();

				}else{

					break;

				}

			}

		}catch(final IOException e){

			// TODO 自動生成された catch ブロック
			e.printStackTrace();

		}

		if(ret.size() != 0){

			return ret.get(0);

		}else{

			return null;

		}

	}

	//----------------------------------------------------------------------------
	//  APIs for user
	//----------------------------------------------------------------------------
	public int getImageSize(final int userId){
		return this.getSizeById(Type.member_illust, userId);
	}

	public int getImageSize(final User user){
		return this.getImageSize(user.getId());
	}

	public List<Image> getImages(final int userId, final int page){
		return this.getImagesByUserId(Type.member_illust, userId, page);
	}

	public List<Image> getImages(final User user, final int page){
		return this.getImages(user.getId(), page);
	}

	public int getMyPixivSize(final int usrId){
		return this.getSizeById(Type.mypixiv_all, usrId);
	}

	public int getMyPixivSize(final User user){
		return this.getMyPixivSize(user.getId());
	}

	public List<User> getMyPixivUsers(final int usrId, final int page){
		return this.getUsersById(Type.mypixiv_all, usrId, page);
	}

	public List<User> getMyPixivUsers(final User user, final int page){
		return this.getMyPixivUsers(user.getId(), page);
	}

	public int getBookmarkedUserSize(final int id){
		return this.getSizeById(Type.bookmark_user_all, id);
	}

	public int getBookmarkedUserSize(final User user){
		return this.getBookmarkedUserSize(user.getId());
	}

	public List<User> getBookmarkedUsers(final int id, final int page){
		return this.getUsersById(Type.bookmark_user_all, id, page);
	}

	public List<User> getBookmarkedUsers(final User user, final int page){
		return this.getBookmarkedUsers(user.getId(), page);
	}

	public int getBookmarkSize(final int id){
		return this.getSizeById(Type.bookmark, id);
	}

	public int getBookmarkSize(final User user){
		return this.getBookmarkSize(user.getId());
	}

	public List<Image> getBookmarks(final int id, final int page){
		return this.getImagesByUserId(Type.bookmark, id, page);
	}

	public List<Image> getBookmarks(final User user, final int page){
		return this.getBookmarks(user.getId(), page);
	}


	//============================================================================
	//  Private methods
	//============================================================================
	private Map<String, String> parseParameters(final String params){

		final Map<String, String> ret = new HashMap<String, String>();

		final Pattern param = Pattern.compile("(\\w+)=(\\w+)");
		final Matcher m = param.matcher(params);
		while(m.find()){

			final String key = m.group(1);
			final String value = m.group(2);

			ret.put(key, value);

		}

		return ret;

	}

	/**
	 * ユーザまたは画像の総数を取得する．
	 *
	 * @param type 取得する総数の種類
	 * @param param 問合せ用パラメータ
	 * @return 取得したユーザまたは画像の総数
	 */
	private int getSize(final Type type, final String param){

		int ret = -1;
		try{

			final URL url = new URL(this.base, String.format("%s.php?%s&%s=%s&c_mode=count", type, param, SessionID, this.session));
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();

			if(con.getResponseCode() == 200){

				final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				final String buf = in.readLine();
				if(buf != null){

					ret = Integer.parseInt(buf);

				}

				in.close();

			}

		}catch(final IOException e){

			e.printStackTrace();

		}

		return ret;

	}

	/**
	 * ID を指定してユーザまたは画像の総数を取得する．
	 *
	 * @param type 取得する総数の種類
	 * @param id 問合せに使用する ID
	 * @return 取得したユーザまたは画像の総数
	 */
	private int getSizeById(final Type type, final int id){
		return this.getSize(type, String.format(IDParamTemplate, id));
	}

	/**
	 * 画像に関する情報を取得する．
	 *
	 * @param type 取得する画像の種類
	 * @param param 問合せ用パラメータ
	 * @param page 取得するページ
	 * @return 取得した画像のリスト
	 */
	private List<Image> getImages(final Type type, final String param, final int page){

		final List<Image> ret = new ArrayList<Image>();
		try{

			final URL url = new URL(this.base, String.format("%s.php?%s&%s=%s&p=%d", type, param, SessionID, this.session, page));
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();

			if(con.getResponseCode() == 200){

				final Reader in = new InputStreamReader(con.getInputStream());
				CSVParser.parse(in, new Handler(){

					@Override
					public void update(final String[] data) {

						try {

							final Image image = new Image(PixivAPI.this, data);
							if(!ret.contains(image)){

								ret.add(image);

							}

						} catch (IOException e) {

							e.printStackTrace();

						}

					}

				});
				in.close();

			}

		}catch(final IOException e){

			e.printStackTrace();

		}

		return ret;

	}

	private List<Image> getImagesByUserId(final Type type, final int id, final int page){
		return this.getImages(type, String.format(IDParamTemplate, id), page);
	}

	private List<Image> findImages(final String param, final int size) throws IOException{

		final List<Image> ret = new ArrayList<Image>();
		for(int i = 0; ret.size() < size; ++i){

			final List<Image> sub = this.getImages(Type.search, param, i);
			if(sub.size() == 0){

				break;

			}

			ret.addAll(sub);

		}


		return ret;

	}

	private List<User> getUsers(final Type type, final String param, final int page){

		final List<User> ret = new ArrayList<User>();
		try{

			final URL url = new URL(this.base, String.format("%s.php?%s&%s=%s&p=%d", type, param, SessionID, this.session, page));
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();

			if(con.getResponseCode() == 200){

				final Reader in = new InputStreamReader(con.getInputStream());
				CSVParser.parse(in, new Handler(){

					@Override
					public void update(final String[] data) {

						try {

							ret.add(new User(PixivAPI.this, data));

						} catch (IOException e) {

							// TODO 自動生成された catch ブロック
							e.printStackTrace();

						}

					}

				});
				in.close();

			}


		}catch(final IOException e){

			e.printStackTrace();

		}

		return ret;

	}

	private List<User> getUsersById(final Type type, final int id, final int page){
		return this.getUsers(type, String.format(IDParamTemplate, id), page);
	}

	//============================================================================
	//  Public static methods
	//============================================================================
	/**
	 * サンプルプログラム．
	 *
	 * @param args ユーザ名，パスワード，タグ検索用キーワード
	 * @throws IOException IOエラーが発生した場合．
	 */
	public static void main(final String[] args) throws IOException{

		final PixivAPI api = new PixivAPI();
		System.out.println("Status: " + api.status());

		System.out.println("= new images on the 1st page");
		for(final Image i : api.getNewImages(0)){

			System.out.println(String.format("%s by %s (%s)", i.getTitle(), i.getAuthorName(), i.getImageURL()));

			final User author = i.getAuthor();
			if(author != null){

				for(final User user : author.getBookmarkedUsers(0)){

					System.out.println(">> " + user);

				}

			}

		}

		System.out.println("= find images by tag");
		for(final Image i : api.findImagesByTag(args[2], 10)){

			System.out.println(String.format("%s by %s (%s)", i.getTitle(), i.getAuthorName(), i.getImageURL()));

		}

		// Login
		if(api.login(args[0], args[1])){

			System.out.println("Profile: " + api.profile());
			System.out.println("My pixiv new images: " + api.getMyPixivNewImageSize());

		}

	}

}
