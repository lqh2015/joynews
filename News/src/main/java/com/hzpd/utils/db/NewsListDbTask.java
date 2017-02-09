package com.hzpd.utils.db;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hzpd.modle.NewsBean;
import com.hzpd.modle.NewsChannelBean;
import com.hzpd.modle.db.NewsBeanDB;
import com.hzpd.modle.db.NewsBeanDBDao;
import com.hzpd.ui.App;
import com.hzpd.ui.interfaces.I_Result;
import com.hzpd.ui.interfaces.I_SetList;
import com.hzpd.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class NewsListDbTask {
    private NewsBeanDBDao newsListDb;

    public NewsListDbTask(Context context) {
        newsListDb = DBHelper.getInstance().getNewsList();
    }

    public void findList(NewsChannelBean channelbean
            , int page, int pageSize
            , I_SetList<NewsBeanDB> callBack) {
        if (channelbean != null) {
            NewsFindTask newsFindTask = new NewsFindTask(channelbean, page, pageSize, callBack);
            newsFindTask.executeOnExecutor(App.executorService);
        }
    }

    public void findisTagid(String tid
            , int page, int pageSize
            , I_SetList<NewsBeanDB> callBack) {

        TagidFindTask newsFindTask = new TagidFindTask(tid, page, pageSize, callBack);
        newsFindTask.executeOnExecutor(App.executorService);
    }

    public void saveList(List<NewsBean> nbList, I_Result callBack) {
        List<NewsBean> temp = new ArrayList<>();
        temp.addAll(nbList);
        NewsSaveTask newsSaveTask = new NewsSaveTask(temp, callBack);
        newsSaveTask.executeOnExecutor(App.executorService);
    }

    public void deleteList(List<String> nbList, I_Result callBack) {
        NewsDeleteTask newsDeleteTask = new NewsDeleteTask(nbList, callBack);
        newsDeleteTask.executeOnExecutor(App.executorService);
    }

    public void asyncDeleteList(List<String> nbList) {
        List<String> temp = new ArrayList<>();
        temp.addAll(nbList);
        for (String nid : temp) {
            try {
                newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nid)).buildDelete().executeDeleteWithoutDetachingEntities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void isRead(String nid, I_Result callBack) {
        NewsIsReadedTask newsIsReadTask = new NewsIsReadedTask(nid, callBack);
        newsIsReadTask.executeOnExecutor(App.executorService);
    }

    public void dropTable(I_Result callBack) {
        NewsClearTableTask newsDropTable = new NewsClearTableTask(callBack);
        newsDropTable.executeOnExecutor(App.executorService);
    }

    public void asyncDropTable() {
        try {
            newsListDb.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NewsFindTask extends AsyncTask<String, String, List<NewsBeanDB>> {
        private int page;
        private int pageSize;
        private String tid;
        private NewsChannelBean channelbean;
        private I_SetList<NewsBeanDB> callBack;
        private String sid;

        public NewsFindTask(NewsChannelBean channelbean
                , int page, int pageSize
                , I_SetList<NewsBeanDB> callBack) {
            this(channelbean, page, pageSize, "0", callBack);
        }

        public NewsFindTask(NewsChannelBean channelbean
                , int page, int pageSize, String sid
                , I_SetList<NewsBeanDB> callBack) {
            super();
            this.page = page - 1;
            this.pageSize = pageSize;
            this.callBack = callBack;
            this.channelbean = channelbean;
            this.sid = sid;
        }

        @Override
        protected List<NewsBeanDB> doInBackground(String... params) {
            if (channelbean == null) {
                return null;
            }
            List<NewsBeanDB> list = null;
            try {
                if (!TextUtils.isEmpty(channelbean.getId())) {
                    list = newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.TagId.eq(channelbean.getId()))
                            .orderDesc(NewsBeanDBDao.Properties.Sort_order)
                            .limit(pageSize).offset(page * pageSize).build().list();
                } else {
                    list = newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Tid.eq(channelbean.getTid()))
                            .orderDesc(NewsBeanDBDao.Properties.Sort_order)
                            .limit(pageSize).offset(page * pageSize).build().list();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<NewsBeanDB> result) {
            if (null != callBack && null != result) {
                callBack.setList(result);
            }
        }

    }

    class TagidFindTask extends AsyncTask<String, String, List<NewsBeanDB>> {
        private int page;
        private int pageSize;
        private String tid;
        private I_SetList<NewsBeanDB> callBack;
        private String sid;

        public TagidFindTask(String tid
                , int page, int pageSize
                , I_SetList<NewsBeanDB> callBack) {
            this(tid, page, pageSize, "0", callBack);
        }

        public TagidFindTask(String tid
                , int page, int pageSize, String sid
                , I_SetList<NewsBeanDB> callBack) {
            super();
            this.page = page - 1;
            this.pageSize = pageSize;
            this.callBack = callBack;
            this.tid = tid;
            this.sid = sid;
        }

        @Override
        protected List<NewsBeanDB> doInBackground(String... params) {
            List<NewsBeanDB> list = null;
            try {
                list = newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Tid.eq(tid))
                        .orderDesc(NewsBeanDBDao.Properties.Sort_order)
                        .limit(pageSize).offset(page * pageSize).build().list();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<NewsBeanDB> result) {
            if (null != callBack) {
                callBack.setList(result);
            }
        }

    }

    class NewsSaveTask extends AsyncTask<String, String, Boolean> {
        private I_Result callBack;
        private List<NewsBean> nbList;

        public NewsSaveTask(List<NewsBean> nbList, I_Result callBack) {
            super();
            this.nbList = nbList;
            this.callBack = callBack;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            boolean flag = false;
            if (null == nbList) {
                return flag;
            }
            try {
                List<NewsBeanDB> list = new ArrayList<NewsBeanDB>();
                for (NewsBean nb : nbList) {
                    if (TextUtils.isEmpty(nb.getNid())) {
                        continue;
                    }
                    if (nb.getType().equals("20")) {
                        continue;
                    }
                    NewsBeanDB nbdb = new NewsBeanDB(nb);
//                    nbdb.setIsreaded("1");
                    list.add(nbdb);
                }

                for (NewsBeanDB nbdb : list) {
                    try {
                        NewsBeanDB newsBeanDB;
                        if (!TextUtils.isEmpty(nbdb.getTagId())) {
                            newsBeanDB = newsListDb.queryBuilder()
                                    .where(NewsBeanDBDao.Properties.Nid.eq(nbdb.getNid()))
                                    .where(NewsBeanDBDao.Properties.TagId.eq(nbdb.getTagId()))
                                    .build().unique();
                        } else {
                            newsBeanDB = newsListDb.queryBuilder()
                                    .where(NewsBeanDBDao.Properties.Tid.eq(nbdb.getTid()))
                                    .where(NewsBeanDBDao.Properties.Nid.eq(nbdb.getNid()))
                                    .build().unique();

                        }
                        if (newsBeanDB != null) {
                            nbdb.setIsreaded(newsBeanDB.getIsreaded());
                        }
                        newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nbdb.getNid())).buildDelete().executeDeleteWithoutDetachingEntities();
                        newsListDb.insert(nbdb);
                        flag = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != callBack) {
                callBack.setResult(result);
            }
        }


    }

    class NewsDeleteTask extends AsyncTask<String, String, Boolean> {
        private I_Result callBack;
        private List<String> nbList;

        public NewsDeleteTask(List<String> nbList, I_Result callBack) {
            super();
            this.nbList = nbList;
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (null == nbList) {
                return false;
            }

            for (String nid : nbList) {
                try {
                    newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nid)).buildDelete().executeDeleteWithoutDetachingEntities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != callBack) {
                callBack.setResult(result);
            }
        }

    }

    class NewsIsReadedTask extends AsyncTask<String, String, Boolean> {
        private String nid;
        private I_Result callBack;

        public NewsIsReadedTask(String nid, I_Result callBack) {
            super();
            this.nid = nid;
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isReaded = false;
            try {
                NewsBeanDB newsBeanDB = newsListDb.queryBuilder().where(NewsBeanDBDao.Properties.Nid.eq(nid)).build().unique();
                if (null != newsBeanDB && "1".equals(newsBeanDB.getIsreaded())) {
                    isReaded = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isReaded;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != callBack) {
                callBack.setResult(result);
            }
        }
    }

    class NewsClearTableTask extends AsyncTask<String, String, Boolean> {

        private I_Result callBack;

        public NewsClearTableTask(I_Result callBack) {
            super();
            this.callBack = callBack;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                newsListDb.deleteAll();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != callBack) {
                callBack.setResult(result);
            }
        }
    }

}
