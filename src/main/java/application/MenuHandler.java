package application;

import application.restaurant_exception.DishAlreadyInMenuException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuHandler {

    public MenuHandler() {
    }


    public static DishType stringConverter(String choice){
        switch (choice.toLowerCase()){
            case "antipasto" :
                return DishType.ANTIPASTI;
            case "primo": return DishType.PRIMI;
            case "secondo": return DishType.SECONDI;
            case "dolce": return DishType.DOLCI;
            default: return null;
        }
    }

    public  static HashMap<DishType,ArrayList<MenuEntry>> initializeMenu(HashMap<DishType,ArrayList<MenuEntry>> menu){
        menu.put(DishType.ANTIPASTI, new ArrayList<>());
        menu.put(DishType.PRIMI, new ArrayList<>());
        menu.put(DishType.SECONDI, new ArrayList<>());
        menu.put(DishType.DOLCI, new ArrayList<>());
        return menu;
    }

    public void checkExistance(String dishName,HashMap<DishType,ArrayList<MenuEntry>> menu){
        for(DishType dt : DishType.values()){
            for (MenuEntry me:menu.get(dt)) {
                if(dishName.equalsIgnoreCase(me.getDish()))
                    throw new DishAlreadyInMenuException("Il piatto risulta gia' inserito nel menu!");
            }
        }
    }

    public void removeDishFromMenu(String dishCode,HashMap<DishType,ArrayList<MenuEntry>> menu){
        MenuEntry m = null;
        DishType d = null;
        for (DishType dt : DishType.values()) {
            for (MenuEntry me:menu.get(dt)) {
                    if(me.getCod().equals(dishCode)){
                        m=me;
                        d=dt;
                    }

            }
        }
        menu.get(d).remove(m);
    }

    public Map<String, List<String>> menuToString(HashMap<DishType,ArrayList<MenuEntry>> menu){
        Map<String, List<String>> menuString = new HashMap<>();
        for (DishType dt:DishType.values()) {
            menuString.put(dt.toString(),new ArrayList<>());
            for (MenuEntry me :menu.get(dt)) {
                menuString.get(dt.toString()).add(me.toString());
            }
        }
        return menuString;
    }
}
