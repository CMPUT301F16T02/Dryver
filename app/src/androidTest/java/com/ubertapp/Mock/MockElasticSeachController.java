package com.ubertapp.Mock;

import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.User;

/**
 * Created by Adam on 11/8/2016.
 */

public class MockElasticSeachController extends ElasticSearchController {

    @Override
    public boolean addUser(User user) {
        return true;
    }
}
