package jp.gr.java_conf.abagames.bulletml;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.*;


/**
 * <b>ChangeDirection</b> is generated by Relaxer based on bulletml.rlx.
 * This class is derived from:
 *
 * <!-- for programmer
 * <elementRule role="changeDirection">
 *   <sequence>
 *     <ref label="direction"/>
 *     <ref label="term"/>
 *   </sequence>
 * </elementRule>
 *
 * <tag name="changeDirection"/>
 * -->
 * <!-- for javadoc -->
 * <pre> &lt;elementRule role="changeDirection"&gt;
 *   &lt;sequence&gt;
 *     &lt;ref label="direction"/&gt;
 *     &lt;ref label="term"/&gt;
 *   &lt;/sequence&gt;
 * &lt;/elementRule&gt;
 * &lt;tag name="changeDirection"/&gt;
 * </pre>
 *
 * @version bulletml.rlx 0.21 (Sun Jun 03 09:44:34 JST 2001)
 * @author  Relaxer 0.13 (http://www.relaxer.org)
 */
public class ChangeDirection implements java.io.Serializable, IRNSContainer, IRNode, IActionChoice
{
    private RNSContext rNSContext_ = new RNSContext(this);
    private Direction direction_;
    private String term_;
    private IRNode parentRNode_;

    /**
     * Creates a <code>ChangeDirection</code>.
     *
     */
    public ChangeDirection()
    {
    }

    /**
     * Creates a <code>ChangeDirection</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public ChangeDirection(RStack stack)
    {
        setup(stack);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public ChangeDirection(Document doc)
    {
        setup(doc.getDocumentElement());
    }

    /**
     * Creates a <code>ChangeDirection</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public ChangeDirection(Element element)
    {
        setup(element);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the File <code>file</code>.
     *
     * @param file
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(File file) throws IOException, SAXException, ParserConfigurationException
    {
        setup(file);
    }

    /**
     * Creates a <code>ChangeDirection</code>
     * by the String representation of URI <code>uri</code>.
     *
     * @param uri
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(String uri) throws IOException, SAXException, ParserConfigurationException
    {
        setup(uri);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the URL <code>url</code>.
     *
     * @param url
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(URL url) throws IOException, SAXException, ParserConfigurationException
    {
        setup(url);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the InputStream <code>in</code>.
     *
     * @param in
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(InputStream in) throws IOException, SAXException, ParserConfigurationException
    {
        setup(in);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the InputSource <code>is</code>.
     *
     * @param is
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(InputSource is) throws IOException, SAXException, ParserConfigurationException
    {
        setup(is);
    }

    /**
     * Creates a <code>ChangeDirection</code> by the Reader <code>reader</code>.
     *
     * @param reader
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public ChangeDirection(Reader reader) throws IOException, SAXException, ParserConfigurationException
    {
        setup(reader);
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the Document <code>doc</code>.
     *
     * @param doc
     */
    public void setup(Document doc)
    {
        setup(doc.getDocumentElement());
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the Element <code>element</code>.
     *
     * @param element
     */
    public void setup(Element element)
    {
        init(element);
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the Stack <code>stack</code>
     * that contains Elements.
     * This constructor is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     */
    public void setup(RStack stack)
    {
        setup(stack.popElement());
    }

    /**
     * @param element
     */
    private void init(Element element)
    {
        RStack stack = new RStack(element);
        rNSContext_.declareNamespace(element);
        setDirection(new Direction(stack));
        term_ = URelaxer.getElementPropertyAsString(stack.popElement());
    }

    /**
     * Creates a DOM representation of the object.
     * Result is appended to the Node <code>parent</code>.
     *
     * @param parent
     */
    public void makeElement(Node parent)
    {
        Document doc;

        if (parent instanceof Document)
        {
            doc = (Document) parent;
        }
        else
        {
            doc = parent.getOwnerDocument();
        }

        Element element = doc.createElementNS("http://www.asahi-net.or.jp/~cs8k-cyu/bulletml", "changeDirection");
        rNSContext_.setupNamespace(element);

        int size;
        this.direction_.makeElement(element);
        URelaxer2.setElementPropertyByString(element, "http://www.asahi-net.or.jp/~cs8k-cyu/bulletml", "term", this.term_, rNSContext_);
        parent.appendChild(element);
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the File <code>file</code>.
     *
     * @param file
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(File file) throws IOException, SAXException, ParserConfigurationException
    {
        setup(file.toURL());
    }

    /**
     * Initializes the <code>ChangeDirection</code>
     * by the String representation of URI <code>uri</code>.
     *
     * @param uri
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(String uri) throws IOException, SAXException, ParserConfigurationException
    {
        setup(UJAXP.getValidDocument(uri));
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the URL <code>url</code>.
     *
     * @param url
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(URL url) throws IOException, SAXException, ParserConfigurationException
    {
        setup(UJAXP.getValidDocument(url));
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the InputStream <code>in</code>.
     *
     * @param in
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(InputStream in) throws IOException, SAXException, ParserConfigurationException
    {
        setup(UJAXP.getValidDocument(in));
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the InputSource <code>is</code>.
     *
     * @param is
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(InputSource is) throws IOException, SAXException, ParserConfigurationException
    {
        setup(UJAXP.getValidDocument(is));
    }

    /**
     * Initializes the <code>ChangeDirection</code> by the Reader <code>reader</code>.
     *
     * @param reader
     * @exception IOException
     * @exception SAXException
     * @exception ParserConfigurationException
     */
    public void setup(Reader reader) throws IOException, SAXException, ParserConfigurationException
    {
        setup(UJAXP.getValidDocument(reader));
    }

    /**
     * Creates a DOM document representation of the object.
     *
     * @exception ParserConfigurationException
     * @return Document
     */
    public Document makeDocument() throws ParserConfigurationException
    {
        Document doc = UJAXP.makeDocument();
        makeElement(doc);

        return (doc);
    }

    /**
     * Gets the RNSContext property <b>rNSContext</b>.
     *
     * @return RNSContext
     */
    public final RNSContext getRNSContext()
    {
        return (rNSContext_);
    }

    /**
     * Sets the RNSContext property <b>rNSContext</b>.
     *
     * @param rNSContext
     */
    public final void setRNSContext(RNSContext rNSContext)
    {
        this.rNSContext_ = rNSContext;
    }

    /**
     * Gets the Direction property <b>direction</b>.
     *
     * @return Direction
     */
    public final Direction getDirection()
    {
        return (direction_);
    }

    /**
     * Sets the Direction property <b>direction</b>.
     *
     * @param direction
     */
    public final void setDirection(Direction direction)
    {
        this.direction_ = direction;

        if (direction != null)
        {
            direction.setParentRNode(this);
        }
    }

    /**
     * Gets the String property <b>term</b>.
     *
     * @return String
     */
    public final String getTerm()
    {
        return (term_);
    }

    /**
     * Sets the String property <b>term</b>.
     *
     * @param term
     */
    public final void setTerm(String term)
    {
        this.term_ = term;
    }

    /**
     * Makes a XML text representation.
     *
     * @return String
     */
    public String makeTextDocument()
    {
        StringBuffer buffer = new StringBuffer();
        makeTextElement(buffer);

        return (new String(buffer));
    }

    /**
     * Makes a XML text representation.
     *
     * @param buffer
     */
    public void makeTextElement(StringBuffer buffer)
    {
        int size;
        String prefix = rNSContext_.getPrefixByUri("http://www.asahi-net.or.jp/~cs8k-cyu/bulletml");
        buffer.append("<");
        URelaxer.makeQName(prefix, "changeDirection", buffer);
        rNSContext_.makeNSMappings(buffer);
        buffer.append(">");
        direction_.makeTextElement(buffer);
        buffer.append("<");
        URelaxer.makeQName(prefix, "term", buffer);
        buffer.append(">");
        buffer.append(URelaxer.escapeCharData(getTerm()));
        buffer.append("</");
        URelaxer.makeQName(prefix, "term", buffer);
        buffer.append(">");
        buffer.append("</");
        URelaxer.makeQName(prefix, "changeDirection", buffer);
        buffer.append(">");
    }

    /**
     * Makes a XML text representation.
     *
     * @param buffer
     */
    public void makeTextElement(PrintWriter buffer)
    {
        int size;
        String prefix = rNSContext_.getPrefixByUri("http://www.asahi-net.or.jp/~cs8k-cyu/bulletml");
        buffer.print("<");
        URelaxer.makeQName(prefix, "changeDirection", buffer);
        rNSContext_.makeNSMappings(buffer);
        buffer.print(">");
        direction_.makeTextElement(buffer);
        buffer.print("<");
        URelaxer.makeQName(prefix, "term", buffer);
        buffer.print(">");
        buffer.print(URelaxer.escapeCharData(getTerm()));
        buffer.print("</");
        URelaxer.makeQName(prefix, "term", buffer);
        buffer.print(">");
        buffer.print("</");
        URelaxer.makeQName(prefix, "changeDirection", buffer);
        buffer.print(">");
    }

    /**
     * Gets the IRNode property <b>parentRNode</b>.
     *
     * @return IRNode
     */
    public final IRNode getParentRNode()
    {
        return (parentRNode_);
    }

    /**
     * Sets the IRNode property <b>parentRNode</b>.
     *
     * @param parentRNode
     */
    public final void setParentRNode(IRNode parentRNode)
    {
        this.parentRNode_ = parentRNode;
    }

    /**
     * Gets child RNodes.
     *
     * @return IRNode[]
     */
    public IRNode[] getRNodes()
    {
        java.util.List classNodes = new java.util.ArrayList();
        classNodes.add(direction_);

        IRNode[] nodes = new IRNode[classNodes.size()];

        return ((IRNode[]) classNodes.toArray(nodes));
    }

    /**
     * Tests if a Element <code>element</code> is valid
     * for the <code>ChangeDirection</code>.
     *
     * @param element
     * @return boolean
     */
    public static boolean isMatch(Element element)
    {
        if (!URelaxer2.isTargetElement(element, "http://www.asahi-net.or.jp/~cs8k-cyu/bulletml", "changeDirection"))
        {
            return (false);
        }

        RStack target = new RStack(element);
        Element child;

        if (!Direction.isMatchHungry(target))
        {
            return (false);
        }

        child = target.popElement();

        if (child == null)
        {
            return (false);
        }

        if (!URelaxer2.isTargetElement(child, "http://www.asahi-net.or.jp/~cs8k-cyu/bulletml", "term"))
        {
            return (false);
        }

        if (!target.isEmptyElement())
        {
            return (false);
        }

        return (true);
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>ChangeDirection</code>.
     * This mehtod is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     * @return boolean
     */
    public static boolean isMatch(RStack stack)
    {
        Element element = stack.peekElement();

        if (element == null)
        {
            return (false);
        }

        return (isMatch(element));
    }

    /**
     * Tests if elements contained in a Stack <code>stack</code>
     * is valid for the <code>ChangeDirection</code>.
     * This method consumes the stack contents during matching operation.
     * This mehtod is supposed to be used internally
     * by the Relaxer system.
     *
     * @param stack
     * @return boolean
     */
    public static boolean isMatchHungry(RStack stack)
    {
        Element element = stack.peekElement();

        if (element == null)
        {
            return (false);
        }

        if (isMatch(element))
        {
            stack.popElement();

            return (true);
        }
        else
        {
            return (false);
        }
    }
}
