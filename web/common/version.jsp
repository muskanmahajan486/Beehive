<%= getVersion()%>
<%!
    /**
     * Returns either the (svn) tagged version or a temporary version number. When the
     * files are tagged by svn then the '$Name:  $' string will be substituted by cvs for
     * a tag. In that case this version number should end up in the project.
     *
     * @return either the tagged version.
     */
    String getVersion() {
      String revision = "$Revision: 436 $";
      String headUrl = "$HeadURL: https://openremote.svn.sourceforge.net/svnroot/openremote/branches/project/Beehive_3_0_0_Betas/web/common/version.jsp $";
      String version = org.openremote.beehive.utils.SvnUtil.getVersionLabel(headUrl,revision);
      return version;
    }
%>
