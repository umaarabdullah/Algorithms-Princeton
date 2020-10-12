import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // n-by-n grid, the n value
    private final int n;

    // grid sites
    private final WeightedQuickUnionUF sites;

    // maker array to mark open sites
    private boolean[] openedSites;

    private final int MAXIMUM_NEIGHBOURS = 4;

    // virtual top node identifier
    private int virtualTopID;

    // virtual bottom node identifier
    private int virtualBtmID;

    // number of open sites
    private int numOpenSites;

    // identifier for the first site of a grid
    private final int TOP_LEFT_ID = 1;

    // grid boundary
    private int siteBoundary;

    // create n-by-n grid of sites, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new java.lang.IllegalArgumentException("n must be greater than 0");
        }

        // initialize the system with n-by-n grid of sites, plus 2 virtual nodes: top virtual node, bottom virtual node
        // top virtual node is in position n^2
        // bottom virtual node is in position n^2 + 1
        this.sites = new WeightedQuickUnionUF(n * n + 3);

        // initalize n^n items
        this.openedSites = new boolean[n * n + 1];

        // set all opendSites items to false
        //Arrays.fill(this.openedSites, false);
        //System.out.println(this.openedSites[n * n]);
        for (int i = 1; i <= n * n; i++) {
            this.openedSites[i] = false;
        }

        this.n = n;
        this.virtualTopID = n * n + 1;
        this.virtualBtmID = n * n + 2;
        this.numOpenSites = 0;
        this.siteBoundary = n * n;
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        // get the 1D coordinates
        int i = this.xyTo1D(row, col);
        //System.out.println("1D : " + i);
        //System.out.println("find(i): " + this.sites.find(i));
        //System.out.println("find(VirtualTop): " + this.sites.find(this.virtualTopID));

        // avoid repeatedly open a site
        if (!this.openedSites[i]) {
            // mark site as open
            this.openedSites[i] = true;

            // connect neighbouring sites
            // first check if the neighbouring sites are open

            // top and bottom sites are offset by n
            // left and right sites are offset by 1

            // create an array to store the position of neighbours
            // top, bottom, left, right
            int[] posNeighbours = { i - this.n, i + this.n, i - 1, i + 1 };

            // for each neighbouring sites, connect the target site to it if it's open
            for (int j = 0; j < this.MAXIMUM_NEIGHBOURS; j++) {
                if (posNeighbours[j] < 0)
                    continue;
                if (posNeighbours[j] > n * n)
                    continue;

                // check if they are within bound and if open and lie on the same row or col i.e side by side or not
                // if so, union i with posNeighbours[j]
                if (this.openedSites[posNeighbours[j]]) {   //this.isInBoundary(posNeighbours[j]) &&-previous implementation

                    // row = ((i + n)-1)/n
                    // col = i - n*(row-1)
                    int tmpRow = (((i + this.n) - 1) / this.n);
                    int tmpCol = (i - this.n * (tmpRow - 1));
                    int neighbourRow = (((posNeighbours[j] + this.n) - 1) / this.n);
                    int neighbourCol = (posNeighbours[j] - this.n * (neighbourRow - 1));

                    //System.out.println("ROW and COL :" + tmpRow + " " + tmpCol);
                    //System.out.println("NEIROW and NEICOL :" + neighbourRow + " " + neighbourCol);
                    //check if they are in same row or column
                    if (tmpRow == neighbourRow || tmpCol == neighbourCol) {
                        this.sites.union(i, posNeighbours[j]);
                    }
                }
            }

            // if the site to open is in the top row, connect it to the virtual top node
            if (i <= this.n) {
                //System.out.println("find(i): " + this.sites.find(i));
                //System.out.println("find(VirtualTop): " + this.sites.find(this.virtualTopID));
                sites.union(i, this.virtualTopID);
            }

            // if the site to open is in the bottom row, connect it to the virtual bottom node
            if (i >= ((this.n * this.n) - this.n)) {
                sites.union(i, this.virtualBtmID);
            }

            // update open sites counter
            this.numOpenSites += 1;
        }
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        // get the 1D coordinates
        int i = this.xyTo1D(row, col);

        return this.openedSites[i];
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        // get the 1D coordinates
        int i = this.xyTo1D(row, col);

        // check if the site in question is connected to the virtual top node
        if (this.sites.find(i) == this.sites.find(this.virtualTopID)) {
            //System.out.println("i: " + i);
            //System.out.println("virtualTop: " + this.virtualTopID);
            return true;
        }
        else
            return false;
    }

    // number of open sites
    public int numberOfOpenSites() {
        return this.numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {

        // check if virtual bottom node connected to virtual top node
        if (this.sites.find(this.virtualBtmID) == this.sites.find(this.virtualTopID)) {
            return true;
        }
        else
            return false;

    }

    /*
     * Turn 2D coordinates to 1D coordinates
     * @return int
     */
    // generating unique 1d array index from row and col, same as determing positon of the elements in the compiler via a formula
    private int xyTo1D(int row, int col) {
        if ((row <= 0 || row > this.n) || (col <= 0 || col > this.n)) {
            throw new java.lang.IndexOutOfBoundsException(
                    "row and col must be within the range 1 - " + this.n + ", " +
                            "inclusively. You entererd row: " + row + ", col: " + col);
        }

        // since by convention the row and column indices are integers between 1 and n
        // we need to offset row by 1
        // and the formula to caclulating indices in 1D is as followed: row * n + col
        return this.n * (row - 1) + col; //     (col - 1)-previous implementation
    }

    /*
     * Check if a site is within the boundary of the grid system
     * Expect argument to be 1D coordinates
     */
    private boolean isInBoundary(int coordinates) {
        return coordinates >= this.TOP_LEFT_ID && coordinates <= this.siteBoundary;
    }


    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        Percolation percolation = new Percolation(n);
        percolation.open(1, 6);
        percolation.open(2, 6);
        percolation.open(3, 6);
        percolation.open(4, 6);
        percolation.open(5, 6);
        percolation.open(5, 5);
        percolation.open(4, 4);
        percolation.open(3, 4);
        percolation.open(2, 4);
        percolation.open(2, 3);
        percolation.open(2, 2);
        percolation.open(2, 1);
        percolation.open(3, 1);
        percolation.open(4, 1);
        percolation.open(5, 1);
        percolation.open(5, 2);
        percolation.open(6, 2);
        percolation.open(5, 4);
        System.out.println(percolation.isFull(2, 1));
    }

}
