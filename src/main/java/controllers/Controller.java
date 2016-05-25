package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Abstract class used to factor uses of {@link Gson}
 */
public abstract class Controller {

    protected Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

}
