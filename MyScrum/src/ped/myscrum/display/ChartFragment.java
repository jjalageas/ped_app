package ped.myscrum.display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import ped.myscrum.gen.R;
import ped.myscrum.serialization.Chart;
import ped.myscrum.serialization.SerialBitmap;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChartFragment extends Fragment {

	private ImageView burndownImage;
	private ImageView tasksImage;

	private CharSequence api_key;
	private String project_id;
	private String sprint_id;
	private Chart chart;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_burndown_charts, null);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		api_key = getArguments().getString("api_key");
		project_id = getArguments().get("project_id").toString();
		sprint_id = getArguments().get("sprint_id").toString();
		chart = new Chart();
		
		burndownImage = (ImageView) rootView.findViewById(R.id.bd_image);
		tasksImage = (ImageView) rootView.findViewById(R.id.task_dist_image);

		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			new BurndownChartRetrieval()
					.execute("http://10.0.2.2:3000/api/owner/projects/"
							+ project_id + "/sprints/" + sprint_id
							+ "/burndown_charts?api_key=" + api_key);
		} else {
			try {

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return rootView;
	}

	public void save_data() throws FileNotFoundException, IOException {
		File save = new File(this.getActivity().getFilesDir() + "charts.bin");
		save.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				save));
		oos.writeObject(chart);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("resource")
	public Chart load_data(File f) throws StreamCorruptedException,
			FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		chart = (Chart) ois.readObject();
		return chart;
	}

	private class BurndownChartRetrieval extends
			AsyncTask<String, String, String> {

		public BurndownChartRetrieval() {
		}

		@Override
		protected String doInBackground(String... url) {
			String result = " ";
			URL url_init;
			HttpURLConnection conn;
			BufferedReader rd;
			String line;
			try {
				url_init = new URL(url[0]);
				conn = (HttpURLConnection) url_init.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				
				JSONObject data;
				data = new JSONObject(result);
				URL url1 = new URL(data.getString("line_chart"));
				InputStream is = (InputStream) url1.getContent();
				chart.burndown_chart = new SerialBitmap(is);
				
				url1 = new URL(data.getString("pie_chart"));
				is = (InputStream) url1.getContent();
				chart.pie_chart = new SerialBitmap(is);
				save_data();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				burndownImage.setImageBitmap(chart.burndown_chart.bitmap);
				tasksImage.setImageBitmap(chart.pie_chart.bitmap);
				System.out.println(burndownImage.toString());
				burndownImage.invalidate();
				tasksImage.invalidate();
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
