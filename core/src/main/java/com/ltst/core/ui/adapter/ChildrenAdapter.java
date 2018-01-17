package com.ltst.core.ui.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.danil.recyclerbindableadapter.library.FilterBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Child;
import com.ltst.core.ui.holder.ChildrenViewHolder;
import com.ltst.core.util.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildrenAdapter extends FilterBindableAdapter<ChildrenAdapter.ChildWrapper, ChildrenViewHolder> {

    private final ChildrenViewHolder.ChildrenClickListener actionListener;
    private final boolean teacherApp;

    public ChildrenAdapter(ChildrenViewHolder.ChildrenClickListener actionListener, boolean teacherApp) {
        this.actionListener = actionListener;
        this.teacherApp = teacherApp;
    }


    @Override
    protected String itemToString(ChildWrapper item) {
        Child child = item.getChild();
        return child.getFirstName() + StringUtils.SPACE + child.getLastName();
    }


    @Override
    protected void onBindItemViewHolder(ChildrenViewHolder viewHolder, int position, int type) {
        viewHolder.setTeacherApp(teacherApp);
        viewHolder.bindView(position, getItem(position), this.actionListener);

    }


    @Override
    protected ChildrenViewHolder viewHolder(View view, int type) {
        return new ChildrenViewHolder(view);
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.view_holder_child;
    }


    public static class ChildWrapper implements Parcelable {

        Child child;
        String ageAndGender;

        public ChildWrapper(Child child, String ageAndGender) {
            this.child = child;
            this.ageAndGender = ageAndGender;
        }

        public Child getChild() {
            return child;
        }

        public String getAgeAndGender() {
            return ageAndGender;
        }

        public void setChild(Child child) {
            this.child = child;
        }

        public void setAgeAndGender(String ageAndGender) {
            this.ageAndGender = ageAndGender;
        }

        public static final String BOY = "boy";
        public static final String GIRL = "girl";
        public static final String ITEM_FOOTER_FORMAT = "%s %d year old";

        public static List<ChildWrapper> wrapChildren(List<Child> children) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Child.SERVER_FORMAT);
            Calendar currentCalendar = Calendar.getInstance();
            Calendar birthdayCalendar = Calendar.getInstance();
            List<ChildWrapper> wrappers = new ArrayList<>(children.size());
            for (Child child : children) {
//                Integer years = 0;
                String diffAge = StringUtils.EMPTY;
                if (!StringUtils.isBlank(child.getBirthDay())) {
                    try {
                        birthdayCalendar.setTime(simpleDateFormat.parse(child.getBirthDay()));
//                        years = CalendarUtil.getDiffYears(birthdayCalendar, currentCalendar);
                        diffAge = CalendarUtil.getDiffAge(birthdayCalendar, currentCalendar);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String gender = child.getGender();
                String displayedGender = null;
                if (!StringUtils.isBlank(gender)) {
                    displayedGender = gender.equals(Child.FEMALE) ? GIRL : BOY;
                }
                String itemFooter = null;
                if (!StringUtils.isBlank(gender)) {
                    itemFooter = displayedGender +
                            StringUtils.SPACE +
                            diffAge;
                }
                wrappers.add(new ChildWrapper(child, itemFooter));
            }
            return wrappers;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.child, flags);
            dest.writeString(this.ageAndGender);
        }

        protected ChildWrapper(Parcel in) {
            this.child = in.readParcelable(Child.class.getClassLoader());
            this.ageAndGender = in.readString();
        }

        public static final Parcelable.Creator<ChildWrapper> CREATOR = new Parcelable.Creator<ChildWrapper>() {
            @Override
            public ChildWrapper createFromParcel(Parcel source) {
                return new ChildWrapper(source);
            }

            @Override
            public ChildWrapper[] newArray(int size) {
                return new ChildWrapper[size];
            }
        };
    }


}
