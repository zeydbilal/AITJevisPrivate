/**
 * Copyright (C) 2015 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEConfig.
 *
 * JEConfig is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * JEConfig is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEConfig. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig;

import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.sql.RelationsManagment;

/**
 * This class handels some userspecific configurations like userrights and
 * custom configuration for the current user.
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class User {

    private boolean isSysAdmin = false;
    private JEVisDataSource _ds;

    public User(JEVisDataSource ds) {
        _ds = ds;
    }

    /**
     * Check if the user is an sys Admin
     *
     * @return true is the user has Sys Admin permissions.
     */
    public boolean isSysAdmin() {
        try {
            return RelationsManagment.isSysAdmin(_ds.getCurrentUser());
        } catch (JEVisException ex) {
            return false;
        }
    }

}
