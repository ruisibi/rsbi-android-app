package com.ruisi.bi.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by berlython on 16/6/7.
 */
public class PushItem implements Serializable {

    private static final long serialVersionUID = 4548989808430047063L;
    public int tid;

    public String pushType;

    public Dim dim;

    public List<KpiJsonItem> kpiJson;

    public Job job;

    public class Dim implements Serializable{

        private static final long serialVersionUID = -1051335625135832192L;

        public String type;

        public Integer id;

        public String colname;

        public String alias;

        public String tname;

        public String tableColKey;

        public String tableColName;

        public String tableName;

        public String dimdesc;

        public String dimord;

        public String grouptype;

        public String iscas;

        public String valType;

        public Integer tid;

        public String dateformat;
    }

    public class KpiJsonItem implements Serializable{

        private static final long serialVersionUID = -295056011919845801L;

        public Integer kpi_id;

        public String kpi_name;

        public String col_name;

        public String aggre;

        public String fmt;

        public String alias;

        public Integer tid;

        public String unit;

        public String rate;

        public String opt;

        public String val1;

        public String val2;
    }

    public class Job implements Serializable{
        private static final long serialVersionUID = -3838458497801637549L;

        public String day;

        public String hour;

        public String minute;
    }
}
