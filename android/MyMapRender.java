/**
 * Created by Bishop87 on 06/03/16.
 */
public class MyMapRender extends DefaultClusterRenderer<Item> {

    private final float mDensity;
    private final IconGenerator mIconClusterGenerator;
    private final IconGenerator mIconItemGenerator;
    private final Bitmap mIconItemGreen;
    private final Bitmap mIconItemOrange;

    private static final int CLUSTER_PADDING = 12;
    private static final int ITEM_PADDING = 7;

    public ItemMapRender(Context context, GoogleMap map, ClusterManager<Item> clusterManager) {
        super(context, map, clusterManager);
        mDensity = context.getResources().getDisplayMetrics().density;

        //preparo l'icona per i cluster, il colore verrà assegnato dinamicamente
        mIconClusterGenerator = new IconGenerator(context);
        mIconClusterGenerator.setContentView(makeSquareTextView(context, CLUSTER_PADDING));
        mIconClusterGenerator.setTextAppearance(com.google.maps.android.R.style.ClusterIcon_TextAppearance);

        //preparo prima le icone dei singoli elementi con un padding specifico
        mIconItemGenerator = new IconGenerator(context);
        mIconItemGenerator.setContentView(makeSquareTextView(context, ITEM_PADDING));
        mIconItemGenerator.setBackground(makeClusterBackground(ContextCompat.getColor(context, R.color.simple_green)));
        mIconItemGreen = mIconItemGenerator.makeIcon();
        mIconItemGenerator.setBackground(makeClusterBackground(ContextCompat.getColor(context, R.color.simple_orange)));
        mIconItemOrange = mIconItemGenerator.makeIcon();
    }

    @Override
    protected void onBeforeClusterItemRendered(Item Item, MarkerOptions markerOptions) {
        // singol Item.
        if(Item.isEnabledItem()) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconItemOrange));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mIconItemGreen));
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Item> cluster, MarkerOptions markerOptions) {
        int clusterSize = getBucket(cluster);

        //loop over all cluster item checking if it's enabled
        for (Item Item: cluster.getItems()) {
            if(Item.isEnabled()){
                mIconClusterGenerator.setBackground(makeClusterBackground(getColor(clusterSize, true)));
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(mIconClusterGenerator.makeIcon(getClusterText(clusterSize)));
                markerOptions.icon(descriptor);
                return;
            }
        }
        mIconClusterGenerator.setBackground(makeClusterBackground(getColor(clusterSize, false)));
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(mIconClusterGenerator.makeIcon(getClusterText(clusterSize)));
        markerOptions.icon(descriptor);
    }

    /**
     * green base: HSV=[155,5; 100%; 76,86%]
     * Value:
     *       size -- value
     *          1 -- 76,86%
     *         25 -- 55,86%
     *         50 -- 35,00%
     *         70 -- 18,06% --> under this is black
     * @param clusterSize cluster size
     * @return Color HSV format
     */
    protected int getColor(int clusterSize, boolean clusterEnabled) {
        float maxSize = 70;
        float size = Math.min((float)clusterSize, maxSize);
        //float value = 0.7686F - (size * 0.0084F);
        //float hue = 155.2F;
        float value = clusterEnabled ? 0.8902F : (0.7686F - (size * 0.0084F));
        float hue = clusterEnabled ? 24.87F : 155.2F;
        //Log.d("COLOR", String.valueOf(value));
        return Color.HSVToColor(new float[]{hue, 1.0F, value});
    }

    private LayerDrawable makeClusterBackground(int color) {
        ShapeDrawable mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        mColoredCircleBackground.getPaint().setColor(color);
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(0x80ffffff);
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int) (mDensity * 3.0F);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }

    private SquareTextView makeSquareTextView(Context context, int padding) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(R.id.text);
        int paddingDpi = (int) (padding * mDensity);
        squareTextView.setPadding(paddingDpi, paddingDpi, paddingDpi, paddingDpi);
        return squareTextView;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}
