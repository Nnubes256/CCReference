package es.nnub.ccreference;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.nnub.ccreference.descriptions.BaseDescriptionAdapter;
import es.nnub.ccreference.descriptions.EnemyDescription;
import es.nnub.ccreference.descriptions.ItemDescription;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DescriptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RES_NAME = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BaseDescriptionAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public DescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param resName Resource name.
     * @return A new instance of fragment DescriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DescriptionFragment newInstance(String resName) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString(RES_NAME, resName);
        fragment.setArguments(args);

        return fragment;
    }

    protected View initalizeLayoutFromBaseResourceName() {
        BaseDescriptionAdapter descriptionAdapter;
        String contentType;
        String contentName;

        String name = RES_NAME;

        ScrollView rootScroll = new ScrollView(requireContext());

        Pattern pattern = Pattern.compile("^([a-z]+)_([a-z_]+)$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.find()){
            MatchResult matchResult = matcher.toMatchResult();
            contentType = matchResult.group(1);
            contentName = matchResult.group(2);
        } else {
            throw new RuntimeException("WRONGLY CRAFTED LINK: " + name);
        }

        switch (contentType) {
            case "enemy":
                descriptionAdapter = new EnemyDescription(requireContext());
                break;
            case "item":
                descriptionAdapter = new ItemDescription(requireContext());
                break;
            default:
                throw new RuntimeException("UNKNOWN TYPE:" + contentType);
        }

        descriptionAdapter.loadResourcesFromName("desc_" + contentType + "_" + contentName);

        adapter = descriptionAdapter;
        View view = descriptionAdapter.initializeView(requireContext(), rootScroll, null);
        rootScroll.addView(view);
        return rootScroll;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(RES_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initalizeLayoutFromBaseResourceName();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
