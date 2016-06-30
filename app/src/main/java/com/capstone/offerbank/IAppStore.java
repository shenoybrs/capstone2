package com.capstone.offerbank;


public interface IAppStore {


    
    
    /**
     * Get the details of an application from the App Store.
     *
     * @param packageName
     */
    AppResponse getAppDetails(String packageName, String url);

    /**
     * Get a list of apps that are in the specified category.
     * 
     * @param category
     *            The App Category, or null to include all categories
     * @param startIndex
     *            The index of the first record to return.
     * @param pageCount
     *            The number of records to return
     * @return
     */
    AppResponse getApps(String group, String category, int startIndex, int pageCount);


}
