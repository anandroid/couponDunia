package cd.coupondunia.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import cd.coupondunia.AppController;

/**
 * Created by anand on 11/08/15.
 */
public abstract class Volley {

    private String TAG="Volley";

    public Map<String, String> params;
    public String url;

    private Request.Priority priority = Request.Priority.NORMAL;



    public Volley()
    {

    }


    public Volley(final Map<String,String> params, String url)
    {






    }

    public void execute(String...args)
    {


         onPreExecute();
         setParamsUrlPriority();

        if(params!=null) {
            String tag_string_req = url;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG + " " + url, response.toString());

                    onPostExecute(response.toString());


                }


            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Log.e(TAG,"Error "+url);

                    onError();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {


                    return params;
                }


                @Override
                public Priority getPriority() {
                    return priority;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    10,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }


    }

    public void executePreHeavy(String... args)
    {

        onPreExecute();

        new preAsyncTask().execute();

    }

    public void executePostHeavy(String... args)
    {

        onPreExecute();

        setParamsUrlPriority();

        if(params!=null) {
            String tag_string_req = url;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG + " " + url, response.toString());

                    new postAsyncTask().execute(response.toString());


                }


            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Log.e(TAG,"Error "+url);

                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    return params;
                }


                @Override
                public Priority getPriority() {
                    return priority;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                    10,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }


    }
    
    protected void onPreExecuteHeavy()
    {

    }
    
    protected void onPostExecuteHeavy(String response)
    {

    }

    public void executeHeavy()
    {
        onPreExecute();

        new AsyncTask<String,Void,String>()
        {

            @Override
            protected String doInBackground(String... params) {
                try
                {

                    onPreExecuteHeavy();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String response)
            {

                    setParamsUrlPriority();

                    if(params!=null) {
                        String tag_string_req = url;
                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG + " " + url, response.toString());

                                new postAsyncTask().execute(response.toString());





                            }


                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                                Log.e(TAG,"Error "+url);

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {

                                return params;
                            }



                            @Override
                            public Priority getPriority() {
                                return priority;
                            }

                        };

                        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                                10,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                    }

                }

        }.execute();


    }
    
    
    
    
    protected abstract void setParamsUrlPriority();
    protected   void onPreExecute()
    {
    };
    protected  void onPostExecute(String response)
    {
    };

    protected void onError()
    {

    }


    protected  void setParams(Map<String,String> params)
    {
        this.params=params;
    }

    protected void setUrl(String url)
    {
        this.url=url;
    }

    protected void setPriority(Request.Priority priority)
    {
        this.priority=priority;
    }

    


    private class preAsyncTask extends AsyncTask<String, Void, String>
    {



        @Override
        protected String doInBackground(String... params)
        {
            try
            {

               onPreExecuteHeavy();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response)
        {

            setParamsUrlPriority();

            if(params!=null) {
                String tag_string_req = url;
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG + " " + url, response.toString());

                        Volley.this.onPostExecute(response.toString());
                        

                        


                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Log.e(TAG,"Error "+url);

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {


                        return params;
                    }



                    @Override
                    public Priority getPriority() {
                        return priority;
                    }

                };

                strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                        10,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }



        }
    }

    private class postAsyncTask extends AsyncTask<String, Void, String>
    {



        @Override
        protected String doInBackground(String... params)
        {
            try
            {

                onPostExecuteHeavy(params[0]);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String response)
        {

             Volley.this.onPostExecute(response);

        }
    }





}
