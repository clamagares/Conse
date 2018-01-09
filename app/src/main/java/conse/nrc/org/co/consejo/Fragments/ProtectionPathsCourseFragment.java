package conse.nrc.org.co.consejo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import conse.nrc.org.co.consejo.Adapters.PagerAdapterFragment;
import conse.nrc.org.co.consejo.Interfaces.MainInterface;
import conse.nrc.org.co.consejo.R;

/**
 * Created by apple on 12/9/17.
 */

public class ProtectionPathsCourseFragment extends Fragment {

    private MainInterface mainInterface;
    private ViewPager protection_paths_course_view_pager;
    private PagerSlidingTabStrip protection_paths_course_tabs_pager;
    private PagerAdapterFragment pagerAdapterFragment;
    private List<Fragment> fragmentList;
    private List<String> tab_tittle;
    public int course_id;
    Context mCtx;


    public ProtectionPathsCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
//        homeInterface.changeTopToolbar(ConfigurationFile.BAR_WITHOUT_BUTTONS);
//        homeInterface.changeBottomToolbar(1);
//        FirebaseClass.setFragmentScreen(getActivity(),ConfigurationFile.RULES_FRAGMENT);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
        mainInterface = (MainInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.protection_paths_course_fragment, container, false);
        protection_paths_course_view_pager = (ViewPager) view.findViewById(R.id.paths_content);
        protection_paths_course_tabs_pager = (PagerSlidingTabStrip) view.findViewById(R.id.protection_path_course_tabs_pager);
        fragmentList = new ArrayList<>();
        tab_tittle = new ArrayList<>();
        ProtectionPathsVBG fragment = new ProtectionPathsVBG();
        ProtectionPathVideoFragment videoFragment = new ProtectionPathVideoFragment();

        fragment.COURSE_ID = course_id;
        videoFragment.video_id = course_id;

        fragmentList.add(fragment);
        fragmentList.add(videoFragment);

        if (course_id == 1) {
            for (String tab : getResources().getStringArray(R.array.vbg_protection_paths_tabs_tittles)) {
                tab_tittle.add(tab);
                Log.d("PROTECTION Fragment", "Added page for tab: " + tab);
            }
        } else if (course_id == 2){
            for (String tab : getResources().getStringArray(R.array.leaders_protection_paths_tabs_tittles)) {
                tab_tittle.add(tab);
                Log.d("PROTECTION Fragment", "Added page for tab: " + tab);
            }
        }
        pagerAdapterFragment = new PagerAdapterFragment(getChildFragmentManager(),fragmentList,tab_tittle);
        protection_paths_course_view_pager.setAdapter(pagerAdapterFragment);
        protection_paths_course_tabs_pager.setViewPager(protection_paths_course_view_pager);

        return view;
    }

}