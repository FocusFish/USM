package fish.focus.uvms.usm.administration.service.role.impl;

import fish.focus.uvms.usm.information.entity.FeatureEntity;
import fish.focus.uvms.usm.information.entity.RoleEntity;
import fish.focus.uvms.usm.administration.domain.ComprehensiveRole;
import fish.focus.uvms.usm.administration.domain.Feature;
import fish.focus.uvms.usm.administration.domain.Role;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides operations for the conversion of Roles between their domain-object
 * and JPA entity representation
 */
@Stateless
public class RoleConverter {

    public RoleConverter() {
    }

    public void updateEntity(RoleEntity entity, ComprehensiveRole role) {
        // create Role
        entity.setName(role.getName());
        entity.setStatus(role.getStatus());
        entity.setDescription(role.getDescription());

        // updating the list of permissions
        if (role.getUpdateFeatures()) {
            List<Long> selectedFeatures = role.getFeatures();
            List<FeatureEntity> featureList = entity.getFeatureList();
            List<FeatureEntity> actualList = new ArrayList<>();
            if (featureList != null && selectedFeatures != null) {
                for (FeatureEntity feature : featureList) {
                    // if former feature is still selected, we will keep it in the
                    // final List
                    if (selectedFeatures.contains(feature.getFeatureId())) {
                        actualList.add(feature);
                        selectedFeatures.remove(feature.getFeatureId());
                    } else {
                        feature.getRoleList().remove(entity);
                    }
                }
            }

            if (selectedFeatures != null) {
                for (Long featureId : selectedFeatures) {
                    FeatureEntity featureEntity = new FeatureEntity();
                    featureEntity.setFeatureId(featureId);
                    featureEntity.setRoleList(new ArrayList<RoleEntity>());

                    actualList.add(featureEntity);
                }
            }
            entity.setFeatureList(actualList);

            // Add child to parent association
            for (FeatureEntity featureEntity : actualList) {
                featureEntity.getRoleList().add(entity);
            }
        }
    }

    Feature convertWithoutRoles(FeatureEntity entity) {
        Feature ret = new Feature();

        ret.setName(entity.getName());
        ret.setFeatureId(entity.getFeatureId());
        ret.setDescription(entity.getDescription());
        ret.setGroup(entity.getGroupName());

        return ret;
    }

    Feature convertWithRoles(FeatureEntity entity) {
        Feature ret = convertWithoutRoles(entity);
        ret.setApplicationName(entity.getApplication().getName());

        List<Role> roles = new ArrayList<>();
        List<RoleEntity> roleEntities = entity.getRoleList();
        for (RoleEntity roleEntity : roleEntities) {
            Role role = new Role();
            role.setRoleId(roleEntity.getRoleId());
            roles.add(role);
        }
        ret.setRoles(roles);

        return ret;
    }

    /**
     * Converts from an entity representation to a domain-object
     * representation.
     *
     * @param src the entity
     * @return the domain-object
     */
    public Role convert(RoleEntity src) {
        Role ret = null;

        if (src != null) {
            ret = new Role();
            ret.setName(src.getName());
            ret.setDescription(src.getDescription());
            ret.setStatus(src.getStatus());
            ret.setRoleId(src.getRoleId());
            ret.setFeatures(convertFeatureList(src.getFeatureList()));
        }

        return ret;
    }

    public ComprehensiveRole convertComprehensively(RoleEntity role) {
        ComprehensiveRole ret = new ComprehensiveRole();

        ret.setStatus(role.getStatus());
        ret.setName(role.getName());
        ret.setRoleId(role.getRoleId());
        List<Long> featureIds = new ArrayList<>();
        for (FeatureEntity feature : role.getFeatureList()) {
            featureIds.add(feature.getFeatureId());
        }
        ret.setFeatures(featureIds);

        return ret;
    }


    private List<Feature> convertFeatureList(List<FeatureEntity> src) {
        List<Feature> ret = null;

        if (src != null) {
            ret = new ArrayList<>();
            for (FeatureEntity item : src) {
                Feature feature = new Feature();
                feature.setName(item.getName());
                feature.setDescription(item.getDescription());
                feature.setFeatureId(item.getFeatureId());
                feature.setApplicationName(item.getApplication().getName());
                feature.setGroup(item.getGroupName());
                ret.add(feature);
            }
        }

        return ret;
    }

    private List<FeatureEntity> convertFeatures(List<Feature> src) {
        List<FeatureEntity> ret = null;

        if (src != null) {
            ret = new ArrayList<>();
            for (Feature item : src) {
                FeatureEntity feature = new FeatureEntity();
                feature.setName(item.getName());
                feature.setDescription(item.getDescription());

                ret.add(feature);
            }
        }

        return ret;
    }

}
