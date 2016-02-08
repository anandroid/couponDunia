package cd.coupondunia.Objects;

import android.support.v7.graphics.Palette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anand on 07/02/16.
 */
public class OutLet {

    public String subFranchiseId;
    public String id;
    public String name;
    public String brandId;
    public String address;
    public String neighbourhoodId;
    public String cityId;
    public String mail;
    public String timings;
    public String cityRank;
    public String latitude;
    public String longitude;
    public String pincode;
    public String landmark;
    public String streetame;
    public String brandName;
    public String url;
    public int numOfCoupons;
    public String neighbourhoodName;
    public String phoneNumber;
    public String cityName;
    public Float distance;

    public ArrayList<Category> categories;

    public String logoUrl;
    public String coverUrl;

    public int dominantColor=-1;
    public Palette palette=null;

    public boolean isClicked=false;


    public void parseAndSet(JSONObject jsonObject)
    {
        try {
            subFranchiseId = jsonObject.getString("SubFranchiseID");
            id=jsonObject.getString("OutletID");
            name=jsonObject.getString("OutletName");
            brandId=jsonObject.getString("BrandID");
            address=jsonObject.getString("Address");
            neighbourhoodId=jsonObject.getString("NeighbourhoodID");
            cityId=jsonObject.getString("CityID");
            mail=jsonObject.getString("Email");
            timings=jsonObject.getString("Timings");
            cityRank=jsonObject.getString("CityRank");
            latitude=jsonObject.getString("Latitude");
            longitude=jsonObject.getString("Longitude");
            pincode=jsonObject.getString("Pincode");
            landmark=jsonObject.getString("Landmark");
            streetame=jsonObject.getString("Streetname");
            brandName=jsonObject.getString("BrandName");
            url=jsonObject.getString("OutletURL");
            numOfCoupons=jsonObject.getInt("NumCoupons");
            neighbourhoodName=jsonObject.getString("NeighbourhoodName");
            phoneNumber=jsonObject.getString("PhoneNumber");
            cityName=jsonObject.getString("CityName");
            distance=new Float(jsonObject.getString("Distance"));
            logoUrl=jsonObject.getString("LogoURL");
            coverUrl=jsonObject.getString("CoverURL");


            JSONArray categoriesArray = jsonObject.getJSONArray("Categories");
            int size=categoriesArray.length();

            categories = new ArrayList<Category>(size);

            for(int i=0;i<size;i++)
            {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);

                Category category = new Category();
                category.name= categoryObject.getString("Name");
                category.offlineId=categoryObject.getString("OfflineCategoryID");
                category.parentId=categoryObject.getString("ParentCategoryID");
                category.type=categoryObject.getString("CategoryType");

                categories.add(category);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
