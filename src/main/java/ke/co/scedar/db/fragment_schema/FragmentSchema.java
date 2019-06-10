package ke.co.scedar.db.fragment_schema;

import ke.co.scedar.db.Databases;
import ke.co.scedar.utils.JvmManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentSchema {

    public static List<String> getFieldsInTable(String tableName){
        return doesTableExist(tableName).getFields();
    }

    public static ComponentMetaData doesTableExist(String tableName){

        //Copy fragment schema to avoid corrupting it
        List<Site> sites = Databases.FRAGMENT_SCHEMA;
        List<String> fields = new ArrayList<>();
        ComponentMetaData componentMetaData = new ComponentMetaData();

        componentMetaData.setComponentType(ComponentType.Table);

        for (Site site : sites){
            for (Table table : site.getTables()){
                if(table.getName().equals(tableName)){
                    componentMetaData.setSiteId(site.getId());
                    componentMetaData.setTableName(table.getName());
                    componentMetaData.setFragmented(table.getIsFragmented());

                    if(table.getIsFragmented()){
                        componentMetaData.setFragmentationMode(table.getFragmentationMetadata()
                                .getFragmentationMode());

                        // FIXME: 6/7/2019 Find a way of making this dynamic.. Ha Ha nakuhurumia mse :)
                        componentMetaData.setFragmentationField(table.getFragmentationMetadata()
                                .getPredicates().get(0).split(":")[0]);
                        componentMetaData.setFragmentationValue(table.getFragmentationMetadata()
                                .getPredicates().get(0).split(":")[2]);
                    }

                    for(Field field : table.getFields()){
                        fields.add(field.getName());
                    }
                    componentMetaData.setFields(fields);
                    return componentMetaData;
                }
            }
        }

        componentMetaData.setHasError(true);
        JvmManager.gc(sites, fields);
        return componentMetaData;
    }

    public static ComponentMetaData doesFieldExist(String tableName, String fieldName){
        ComponentMetaData componentMetaData = doesTableExist(tableName);

        if(!componentMetaData.hasError()){

            componentMetaData.setComponentType(ComponentType.Field);

            //Copy fragment schema to avoid corrupting it
            List<Site> sites = Databases.FRAGMENT_SCHEMA;


            JvmManager.gc(sites);
            return componentMetaData;
        }else return componentMetaData;
    }

}
