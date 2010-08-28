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
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

class CSVParser {

	private enum State{

		Default, Quoted, Escaped, CR, LF

	};

	private static final char Quote = '\"';
	private static final char Comma = ',';
	private static final char Escape = '\\';
	private static final char CR = '\r';
	private static final char LF = '\n';
	private static final int EOF = -1;

	public static void parse(final Reader in, final Handler callback) throws IOException{

		final PushbackReader cin = new PushbackReader(new BufferedReader(in));
		final List<String> holder = new ArrayList<String>();

		StringBuffer buffer = new StringBuffer();

		State stat = State.Default;
		for(int c = 0; (c = cin.read()) != EOF;){

			switch(stat){
			case Default:

				switch(c){

				case Quote:

					stat = State.Quoted;
					break;

				case Comma:

					holder.add(buffer.toString());
					buffer = new StringBuffer();
					break;

				case CR:

					stat = State.CR;
					break;

				case LF:

					callback.update(holder.toArray(new String[0]));
					stat = State.Default;
					holder.clear();
					break;

				default:

					buffer.append((char)c);

				}

				break;

			case Quoted:

				if(c == Quote){

					stat = State.Default;

				}else if(c == Escape){

					stat = State.Escaped;

				}else{

					buffer.append((char)c);

				}

				break;

			case Escaped:

				buffer.append(Escape);
				buffer.append((char)c);

				stat = State.Quoted;

				break;

			case CR:

				if(c == LF){

					stat = State.LF;

				}else{

					stat = State.Default;
					callback.update(holder.toArray(new String[0]));
					cin.unread(c);

				}
				break;

			}

		}

	}

	public interface Handler{

		public void update(final String[] data);

	}

}
