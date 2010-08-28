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

public class PixivAPI {

	private final URL base;

	private String session = null;

	//============================================================================
	//  Constants
	//============================================================================
	private static final String BaseURL = "http://iphone.pxv.jp/iphone/";
	private static final String SessionID = "PHPSESSID";

	private static final String NewImages = "new_illust";
	private static final String MyPixivNewImages = "mypixiv_new_illust";
	private static final String BookmarkedUserNewImages = "bookmark_user_new_illust";
	private static final String Ranking = "ranking";
	private static final String Search = "search";
	private static final String SearchUser = "search_user";

	private static final String DummyParameter = "dummy=0";
	private static final String Daily = "mode=day";
	private static final String Weekly = "mode=week";
	private static final String Monthly = "mode=month";

	private static final String IDParamTemplate = "id=%d";

	private static final String UTF8 = "UTF-8";

	//============================================================================
	//  Constructors
	//============================================================================
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

		return this.session != null;

	}

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
		return this.getSize(NewImages, DummyParameter);
	}

	public List<Image> getNewImages(final int size){
		return this.getImages(NewImages, DummyParameter, size);
	}

	public int getMyPixivNewImageSize(){
		return this.getSize(MyPixivNewImages, DummyParameter);
	}

	public List<Image> getMyPixivNewImages(final int size){
		return this.getImages(MyPixivNewImages, DummyParameter, size);
	}

	public int getBookmarkedUserNewImageSize(){
		return this.getSize(BookmarkedUserNewImages, DummyParameter);
	}

	public List<Image> getBookmarkedUserNewImages(final int size){
		return this.getImages(BookmarkedUserNewImages, DummyParameter, size);
	}

	public int getDailyRankingImageSize(){
		return this.getSize(Ranking, Daily);
	}

	public List<Image> getDailyRankingImages(final int size){
		return this.getImages(Ranking, Daily, size);
	}

	public int getWeeklyRankingImageSize(){
		return this.getSize(Ranking, Weekly);
	}

	public List<Image> getWeeklyRankingImages(final int size){
		return this.getImages(Ranking, Weekly, size);
	}

	public int getMonthlyRankingImageSize(){
		return this.getSize(Ranking, Monthly);
	}

	public List<Image> getMonthlyRankingImages(final int size){
		return this.getImages(Ranking, Monthly, size);
	}


	//----------------------------------------------------------------------------
	//  APIs for search
	//----------------------------------------------------------------------------
	public List<Image> findImagesByTag(final String keyword, final int size) throws IOException{
		return this.getImages(Search, String.format("s_mode=s_tag&word=%s", URLEncoder.encode(keyword , UTF8)), size);
	}

	public List<Image> findImagesByTitle(final String keyword, final int size) throws IOException{
		return this.getImages(Search, String.format("s_mode=s_tc&word=%s", URLEncoder.encode(keyword , UTF8)), size);
	}

	public List<User> findUsers(final String name, final int size) throws IOException{
		return this.getUsers(SearchUser, String.format("nick=%s", URLEncoder.encode(name , UTF8)), size);
	}

	//----------------------------------------------------------------------------
	//  APIs for user
	//----------------------------------------------------------------------------
	public int getImageSize(final int userId){
		return this.getSizeById("member_illust", userId);
	}

	public int getImageSize(final User user){
		return this.getImageSize(user.getId());
	}

	public List<Image> getImages(final int userId, final int size){
		return this.getImagesById("member_illust", userId, size);
	}

	public List<Image> getImages(final User user, final int size){
		return this.getImages(user.getId(), size);
	}

	public int getMyPixivSize(final int usrId){
		return this.getSizeById("mypixiv_all", usrId);
	}

	public int getMyPixivSize(final User user){
		return this.getMyPixivSize(user.getId());
	}

	public List<User> getMyPixivUsers(final int usrId, final int size){
		return this.getUsersById("mypixiv_all", usrId, size);
	}

	public List<User> getMyPixivUsers(final User user, final int size){
		return this.getMyPixivUsers(user.getId(), size);
	}

	public int getBookmarkedUserSize(final int id){
		return this.getSizeById("bookmark_user_all", id);
	}

	public int getBookmarkedUserSize(final User user){
		return this.getBookmarkedUserSize(user.getId());
	}

	public List<User> getBookmarkedUsers(final int id, final int size){
		return this.getUsersById("bookmark_user_all", id, size);
	}

	public List<User> getBookmarkedUsers(final User user, final int size){
		return this.getBookmarkedUsers(user.getId(), size);
	}

	public int getBookmarkSize(final int id){
		return this.getSizeById("bookmark", id);
	}

	public int getBookmarkSize(final User user){
		return this.getBookmarkSize(user.getId());
	}

	public List<Image> getBookmarks(final int id, final int size){
		return this.getImagesById("bookmark", id, size);
	}

	public List<Image> getBookmarks(final User user, final int size){
		return this.getBookmarks(user.getId(), size);
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

	private int getSize(final String type, final String param){

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

	private int getSizeById(final String type, final int id){
		return this.getSize(type, String.format(IDParamTemplate, id));
	}

	private List<Image> getImages(final String type, final String param, final int size){

		final List<Image> ret = new ArrayList<Image>();
		try{

			for(int i = 0; ret.size() < size; ++i){

				final int current = ret.size();
				final URL url = new URL(this.base, String.format("%s.php?%s&%s=%s&p=%d", type, param, SessionID, this.session, i));
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();

				if(con.getResponseCode() == 200){

					final Reader in = new InputStreamReader(con.getInputStream());
					CSVParser.parse(in, new Handler(){

						@Override
						public void update(final String[] data) {

							if(ret.size() < size){

								try {

									ret.add(new Image(PixivAPI.this, data));

								} catch (IOException e) {
									// TODO 自動生成された catch ブロック
									e.printStackTrace();

								}

							}

						}

					});
					in.close();

				}

				if(current == ret.size()){

					break;

				}

			}

		}catch(final IOException e){

			e.printStackTrace();

		}

		return ret;

	}

	private List<Image> getImagesById(final String type, final int id, final int size){
		return this.getImages(type, String.format(IDParamTemplate, id), size);
	}

	private List<User> getUsers(final String type, final String param, final int size){

		final List<User> ret = new ArrayList<User>();
		try{

			for(int i = 0; ret.size() < size; ++i){

				final int current = ret.size();

				final URL url = new URL(this.base, String.format("%s.php?%s&%s=%s&p=%d", type, param, SessionID, this.session, i));
				final HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();

				if(con.getResponseCode() == 200){

					final Reader in = new InputStreamReader(con.getInputStream());
					CSVParser.parse(in, new Handler(){

						@Override
						public void update(final String[] data) {

							if(ret.size() < size){

								try {

									ret.add(new User(PixivAPI.this, data));

								} catch (IOException e) {

									// TODO 自動生成された catch ブロック
									e.printStackTrace();

								}

							}

						}

					});
					in.close();

				}

				if(current == ret.size()){

					break;

				}

			}

		}catch(final IOException e){

			e.printStackTrace();

		}

		return ret;

	}

	private List<User> getUsersById(final String type, final int id, final int size){
		return this.getUsers(type, String.format(IDParamTemplate, id), size);
	}

	//============================================================================
	//  Public static methods
	//============================================================================
	public static void main(final String[] args) throws IOException{

		final PixivAPI api = new PixivAPI();
		System.out.println("Status: " + api.status());

		System.out.println("= 10 new images");
		for(final Image i : api.getNewImages(10)){

			System.out.println(String.format("%s by %s (%s)", i.getTitle(), i.getAuthorName(), i.getImageURL()));

		}

		// Login
		if(api.login(args[0], args[1])){

			System.out.println("Profile: " + api.profile());
			System.out.println("My pixiv new images: " + api.getMyPixivNewImageSize());

		}


	}

}
