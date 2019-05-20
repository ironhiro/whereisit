package com.mytoilet.whereisit;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if(oldVersion==1)
        {
            schema.get("Toilet")
                .addField("toilet_addr1",String.class)
                .addField("toilet_addr2",String.class)
                    .removeField("toilet_addr");
            oldVersion++;
        }

    }
}
