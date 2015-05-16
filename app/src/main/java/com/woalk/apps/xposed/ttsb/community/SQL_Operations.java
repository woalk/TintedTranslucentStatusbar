package com.woalk.apps.xposed.ttsb.community;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;

import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q.DataLoadedListener;

public final class SQL_Operations {
	private SQL_Operations() {
	}

	/**
	 * Class for processing a general <b>q</b>uery to the PHP scripts of TTSB
	 * Community.<br />
	 * <br />
	 * Instantiate this, set your Listeners, add request data per
	 * {@code addNameValuePair(...)} and start the request with {@link #exec()}.
	 * 
	 * @author woalk
	 */
	public static class Q {
		private String url;
		private List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>();
		private PreExecuteListener mPreExecuteL;
		private PostExecuteListener mPostExecuteL;
		private DataLoadedListener mDataLoadedL;

		public static interface PreExecuteListener {
			/**
			 * Called in the UI Thread before the execution begins.
			 */
			public abstract void onPreExecute();
		}

		public static interface DataLoadedListener {
			/**
			 * Called in the <b>worker</b> thread when the data is retrieved.
			 * 
			 * @param data
			 *            The data that was loaded, to process it further.
			 * @return A {@link Bundle} with data that is passed to
			 *         {@link PostExecuteListener#onPostExecute(Bundle)} to make
			 *         it accessible from there.
			 * @throws JSONException
			 *             When an error in the JSON array appears when
			 *             processing.
			 */
			public abstract Bundle onDataLoaded(JSONArray data)
					throws JSONException;
		}

		public static interface PostExecuteListener {
			/**
			 * Called in the UI Thread when execution and data processing is
			 * finished.
			 * 
			 * @param processed
			 *            The processed data of this query, returned by
			 *            {@link DataLoadedListener#onDataLoaded(JSONArray)}.
			 */
			public abstract void onPostExecute(Bundle processed);
		}

		/**
		 * Create a {@link Q} instance for processing.
		 * 
		 * @param url
		 *            The URL to the HTTP document to address the query to.
		 */
		public Q(String url) {
			this.url = url;
		}

		/**
		 * Set the {@link PreExecuteListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new PreExecuteListener() { ... }</code>.
		 */
		public void setPreExecuteListener(PreExecuteListener l) {
			mPreExecuteL = l;
		}

		/**
		 * Set the {@link PostExecuteListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new PostExecuteListener() { ... }</code>.
		 */
		public void setPostExecuteListener(PostExecuteListener l) {
			mPostExecuteL = l;
		}

		/**
		 * Set the {@link DataLoadedListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new DataLoadedListener() { ... }</code>.
		 */
		public void setDataLoadedListener(DataLoadedListener l) {
			mDataLoadedL = l;
		}

		/**
		 * Adds a new NameValuePair to include in the {@link HttpPost}.
		 * 
		 * @param nvp
		 *            The {@link NameValuePair} to add.
		 */
		public void addNameValuePair(NameValuePair nvp) {
			mNameValuePairs.add(nvp);
		}

		/**
		 * Adds a new NameValuePair to include in the {@link HttpPost}.
		 * 
		 * @param name
		 *            The name key.
		 * @param value
		 *            The value to assign to the specified key.
		 */
		public void addNameValuePair(String name, String value) {
			mNameValuePairs.add(new BasicNameValuePair(name, value));
		}

		/**
		 * Starts this query with the assigned values.<br />
		 * The execution will run in its own thread per {@link AsyncTask} and
		 * call the Listeners of this object at specific points.
		 */
		public void exec() {
			new HttpPostTask().execute((Void) null);
		}

		private class HttpPostTask extends AsyncTask<Void, String, Bundle> {

			@Override
			protected Bundle doInBackground(Void... params) {
				String result = "";
				try {
					// get database connection & package entries
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();

					InputStream is = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "utf-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					result = sb.toString();
					JSONArray data = new JSONArray(result);
					Bundle processedData = mDataLoadedL.onDataLoaded(data);
					return processedData;
				} catch (UnknownHostException e) {
					// TODO: no connection
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

			@Override
			protected void onPreExecute() {
				if (mPreExecuteL != null)
					mPreExecuteL.onPreExecute();
			}

			@Override
			protected void onPostExecute(Bundle result) {
				if (mPostExecuteL != null)
					mPostExecuteL.onPostExecute(result);
			}
		}

	}

	/**
	 * Class for processing a general <b>q</b>uery to the PHP scripts of TTSB
	 * Community, returning the raw result.<br />
	 * <br />
	 * Instantiate this, set your Listeners, add request data per
	 * {@code addNameValuePair(...)} and start the request with {@link #exec()}.
	 * 
	 * @author woalk
	 */
	public static class CustomQ {
		private String url;
		private List<NameValuePair> mNameValuePairs = new ArrayList<NameValuePair>();
		private PreExecuteListener mPreExecuteL;
		private PostExecuteListener mPostExecuteL;
		private PreHttpPostListener mPreHttp;
		private HttpResultListener mResultL;

		public static interface PreExecuteListener {
			/**
			 * Called in the UI Thread before the execution begins.
			 */
			public abstract void onPreExecute();
		}

		public static interface PreHttpPostListener {
			/**
			 * Called in the <b>worker</b> thread before any execution of
			 * Http-things.
			 * 
			 * @param q
			 *            The calling {@link CustomQ}.
			 */
			public abstract void onPreHttpPost(CustomQ q);
		}

		public static interface HttpResultListener {
			/**
			 * Called in the <b>worker</b> thread when the data is retrieved.
			 * 
			 * @param result
			 *            The data that was loaded, to process it further.
			 * @return A {@link Bundle} with data that is passed to
			 *         {@link PostExecuteListener#onPostExecute(Bundle)} to make
			 *         it accessible from there.
			 */
			public abstract Bundle onHttpResult(String result);
		}

		public static interface PostExecuteListener {
			/**
			 * Called in the UI Thread when execution and data processing is
			 * finished.
			 * 
			 * @param processed
			 *            The processed data of this query, returned by
			 *            {@link DataLoadedListener#onDataLoaded(JSONArray)}.
			 */
			public abstract void onPostExecute(Bundle processed);
		}

		/**
		 * Create a {@link Q} instance for processing.
		 * 
		 * @param url
		 *            The URL to the HTTP document to address the query to.
		 */
		public CustomQ(String url) {
			this.url = url;
		}

		/**
		 * Set the {@link PreExecuteListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new PreExecuteListener() { ... }</code>.
		 */
		public void setPreExecuteListener(PreExecuteListener l) {
			mPreExecuteL = l;
		}

		/**
		 * Set the {@link PostExecuteListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new PostExecuteListener() { ... }</code>.
		 */
		public void setPostExecuteListener(PostExecuteListener l) {
			mPostExecuteL = l;
		}

		/**
		 * Set the {@link HttpResultListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new HttpResultListener() { ... }</code>.
		 */
		public void setHttpResultListener(HttpResultListener l) {
			mResultL = l;
		}

		/**
		 * Set the {@link PreHttpPostListener} of this query object.
		 * 
		 * @param l
		 *            The listener to assign.<br />
		 *            Can be easily defined per
		 *            <code>new PreHttpPostListener() { ... }</code>.
		 */
		public void setPreHttpPostListener(PreHttpPostListener l) {
			this.mPreHttp = l;
		}

		/**
		 * Adds a new NameValuePair to include in the {@link HttpPost}.
		 * 
		 * @param nvp
		 *            The {@link NameValuePair} to add.
		 */
		public void addNameValuePair(NameValuePair nvp) {
			mNameValuePairs.add(nvp);
		}

		/**
		 * Adds a new NameValuePair to include in the {@link HttpPost}.
		 * 
		 * @param name
		 *            The name key.
		 * @param value
		 *            The value to assign to the specified key.
		 */
		public void addNameValuePair(String name, String value) {
			mNameValuePairs.add(new BasicNameValuePair(name, value));
		}

		/**
		 * Starts this query with the assigned values.<br />
		 * The execution will run in its own thread per {@link AsyncTask} and
		 * call the Listeners of this object at specific points.
		 */
		public void exec() {
			new HttpPostTask().execute((Void) null);
		}

		private class HttpPostTask extends AsyncTask<Void, String, Bundle> {

			@Override
			protected Bundle doInBackground(Void... params) {
				if (mPreHttp != null)
					mPreHttp.onPreHttpPost(CustomQ.this);
				String result = "";
				try {
					// get database connection & package entries
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();

					InputStream is = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "utf-8"), 8);
					String line = null;
					while ((line = reader.readLine()) != null) {
						result += line;
					}
					is.close();
					Bundle processedData = mResultL.onHttpResult(result);
					return processedData;
				} catch (UnknownHostException e) {
					// TODO: no connection
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}

			@Override
			protected void onPreExecute() {
				if (mPreExecuteL != null)
					mPreExecuteL.onPreExecute();
			}

			@Override
			protected void onPostExecute(Bundle result) {
				if (mPostExecuteL != null)
					mPostExecuteL.onPostExecute(result);
			}
		}

	}
}
